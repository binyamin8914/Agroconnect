"""Escribe en el resumen del pipeline (GITHUB_STEP_SUMMARY) el resultado de pruebas y cobertura."""
import glob
import os
import xml.etree.ElementTree as ET

total = fallas = omitidas = 0
filas = []
for archivo in sorted(glob.glob("build/test-results/test/*.xml")):
    suite = ET.parse(archivo).getroot()
    t = int(suite.get("tests"))
    f = int(suite.get("failures")) + int(suite.get("errors"))
    s = int(suite.get("skipped"))
    total, fallas, omitidas = total + t, fallas + f, omitidas + s
    nombre = suite.get("name").replace("cl.agroconnect.pedidos.", "")
    filas.append(f"| {nombre} | {t} | {f} | {float(suite.get('time')):.2f} s |")

cobertura = "s/i"
reporte = "build/reports/jacoco/test/jacocoTestReport.xml"
if os.path.exists(reporte):
    for c in ET.parse(reporte).getroot().findall("counter"):
        if c.get("type") == "LINE":
            cub, fal = int(c.get("covered")), int(c.get("missed"))
            cobertura = f"{100 * cub / (cub + fal):.1f} %"

texto = [
    "## Resultado de pruebas automatizadas",
    "",
    f"**Pruebas:** {total} ejecutadas, {total - fallas - omitidas} exitosas, {fallas} fallidas, {omitidas} omitidas  ",
    f"**Cobertura de líneas (JaCoCo):** {cobertura} (umbral del Quality Gate: 80 %)",
    "",
    "| Clase de prueba | Pruebas | Fallas | Tiempo |",
    "|---|---|---|---|",
    *filas,
]
destino = os.environ.get("GITHUB_STEP_SUMMARY")
if destino:
    with open(destino, "a", encoding="utf-8") as fh:
        fh.write("\n".join(texto) + "\n")
print("\n".join(texto))
