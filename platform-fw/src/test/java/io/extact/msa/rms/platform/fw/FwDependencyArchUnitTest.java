package io.extact.msa.rms.platform.fw;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;
import static com.tngtech.archunit.library.Architectures.*;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

@AnalyzeClasses(packages = "io.extact.msa.rms", importOptions = ImportOption.DoNotIncludeTests.class)
class FwDependencyArchUnitTest {

    // ---------------------------------------------------------------------
    // platform.fwパッケージ内部の依存関係の定義
    // ---------------------------------------------------------------------

    /**
     * commons-csvへの依存はfile.ioパッケージのみの定義
     * <pre>
     * ・org.apache.commons.csv.*に依存するのはpersistence.file.ioパッケージのみであること
     * </pre>
     */
    @ArchTest
    static final ArchRule test_commons_csvへの依存はioパッケージのみの定義 =
            noClasses()
                .that()
                    .resideInAPackage("io.extact.msa.rms.platform.core..")
                    .and().resideOutsideOfPackage("io.extact.msa.rms.platform.fw.persistence.file.io..")
            .should()
                .dependOnClassesThat()
                    .resideInAnyPackage(
                        "org.apache.commons.csv.."
                        );


    // ---------------------------------------------------------------------
    // platform.fwパッケージ配下のパッケージ依存関係
    // ---------------------------------------------------------------------

    /**
     * platform.fwパッケージ配下のパッケージ依存関係
     * <pre>
     * ・webapiレイヤはどのレイヤからも依存されていないこと（webapiレイヤは誰も使ってはダメ））
     * ・serviceレイヤwebapiレイヤからのみ依存を許可（serviceレイヤを使って良いのはwebapiレイヤのみ）
     * ・persistenceレイヤはserviceレイヤからのみ依存を許可（persistenceレイヤを使って良いのはserviceレイヤのみ）
     * ・domianレイヤはすべてのレイヤから使ってよい
     * </pre>
     */
    @ArchTest
    static final ArchRule test_レイヤー間の依存関係の定義 = layeredArchitecture()
            .layer("webapi").definedBy("io.extact.msa.rms.platform.fw.webapi..")
            .layer("service").definedBy("io.extact.msa.rms.platform.fw.service..")
            .layer("persistence").definedBy("io.extact.msa.rms.platform.fw.persistence..")
            .layer("domain").definedBy(
                    "io.extact.msa.rms.platform.fw.domain..",
                    "io.extact.msa.rms.platform.fw.exception..",
                    "io.extact.msa.rms.platform.fw.login..")

            .whereLayer("webapi").mayNotBeAccessedByAnyLayer()
            .whereLayer("service").mayOnlyBeAccessedByLayers("webapi")
            .whereLayer("persistence").mayOnlyBeAccessedByLayers("service")
            .whereLayer("domain").mayOnlyBeAccessedByLayers("webapi", "service", "persistence");
}
