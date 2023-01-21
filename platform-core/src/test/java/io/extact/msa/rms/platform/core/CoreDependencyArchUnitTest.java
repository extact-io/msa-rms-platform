package io.extact.msa.rms.platform.core;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

@AnalyzeClasses(packages = "io.extact.msa.rms", importOptions = ImportOption.DoNotIncludeTests.class)
class CoreDependencyArchUnitTest {

    // ---------------------------------------------------------------------
    // platform.coreパッケージ内部の依存関係の定義
    // ---------------------------------------------------------------------

    /**
     * jose4jとAuth0への依存はprovider.implパッケージのみの定義
     * <pre>
     * ・jose4jとAuth0へはprovider.impljパッケージでしか依存していないこと
     * </pre>
     */
    @ArchTest
    static final ArchRule test_JWT実装への依存はimplパッケージのみの定義 =
            noClasses()
                .that()
                    .resideInAPackage("io.extact.msa.rms..")
                    .and().resideOutsideOfPackage("io.extact.msa.rms.platform.core.jwt.provider.impl..")
            .should()
                .dependOnClassesThat()
                    .resideInAnyPackage(
                        "org.jose4j.jwt..",
                        "com.auth0.jwt.."
                        );
    /**
     * jwtパッケージの依存関係の定義
     * <pre>
     * ・jwtパッケージ直下のクラスはjwtの実装依存のimplパッケージに依存してないこと
     * </pre>
     */
    @ArchTest
    static final ArchRule test_jwtパッケージ内部の依存関係の定義 =
            noClasses()
                .that()
                    .resideInAPackage("io.extact.msa.rms.platform.core.jwt")
                .should()
                    .dependOnClassesThat()
                        .resideInAnyPackage(
                                "io.extact.msa.rms.platform.core.jwt.provider.impl.."
                                );
}
