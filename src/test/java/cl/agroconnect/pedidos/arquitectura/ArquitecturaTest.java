package cl.agroconnect.pedidos.arquitectura;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

/**
 * Fitness functions arquitectónicas (RNF-06 Mantenibilidad).
 * Si alguien rompe las reglas de dependencia entre capas, el pipeline falla.
 */
@AnalyzeClasses(packages = "cl.agroconnect.pedidos", importOptions = ImportOption.DoNotIncludeTests.class)
class ArquitecturaTest {

    @ArchTest
    static final ArchRule dominioIndependiente = noClasses()
            .that().resideInAPackage("..dominio..")
            .should().dependOnClassesThat()
            .resideInAnyPackage("..api..", "..aplicacion..", "..infraestructura..", "..integracion..",
                    "org.springframework..")
            .because("el dominio no debe depender de frameworks ni de otras capas");

    @ArchTest
    static final ArchRule apiNoAccedeADatos = noClasses()
            .that().resideInAPackage("..api..")
            .should().dependOnClassesThat()
            .resideInAnyPackage("..infraestructura..", "..integracion..")
            .because("la presentación solo usa casos de uso (evita el antipatrón de capas saltadas)");

    @ArchTest
    static final ArchRule aplicacionNoConoceDetalles = noClasses()
            .that().resideInAPackage("..aplicacion..")
            .should().dependOnClassesThat()
            .resideInAnyPackage("..api..", "..infraestructura..", "..integracion..")
            .because("los casos de uso dependen de interfaces (puertos), no de implementaciones");

    @ArchTest
    static final ArchRule sinCiclos = slices().matching("cl.agroconnect.pedidos.(*)..")
            .should().beFreeOfCycles();
}
