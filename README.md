# AgroConnect · servicio-pedidos

Componente mínimo del **Proyecto Final de Arquitectura de Software** (Gabriel Hidalgo, Benjamín Muñoz).
Implementa el caso de uso **RF-04 Registrar pedido** de la plataforma AgroConnect y sirve para demostrar el pipeline de
integración y entrega continuas (CI/CD) con GitHub Actions.

## Qué hace

`POST /api/pedidos` valida al cliente, verifica y reserva stock, calcula la tarifa de despacho, registra el pedido y
publica el evento *PedidoRegistrado*. Ese evento lo reciben dos observadores: uno solicita el despacho al transportista
elegido y otro genera la confirmación para el cliente.

| Patrón | Dónde | Problema que resuelve |
|---|---|---|
| **Adapter** | `integracion/transportistas` (`TransAgroAdapter`, `RutaSurAdapter` → `TransportistaPort`) | Cada transportista tiene una API distinta (XML, mapas, gramos). Agregar uno nuevo no modifica Pedidos (RNF-05). |
| **Observer** | `dominio/evento` (`PublicadorEventosPedido`, `PedidoObserver`) | Pedidos no conoce a Despacho ni a Notificaciones; se agregan interesados sin tocar el caso de uso. |
| **Strategy** | `dominio/tarifa` (`TarifaEstandar`, `TarifaExpress`, `TarifaRetiroEnBodega`) | Cada modalidad de despacho calcula su tarifa con otro algoritmo, sin condicionales en el servicio. |

## Estructura (vista de desarrollo)

```
src/main/java/cl/agroconnect/pedidos
├── api/                         # Controlador REST, DTO y manejo de errores
├── aplicacion/                  # Caso de uso, puertos (interfaces) y observadores
├── dominio/                     # Entidades, reglas, Strategy y Observer (sin dependencias de Spring)
├── integracion/transportistas/  # Adaptadores de transportistas (Adapter)
└── infraestructura/             # Repositorios en memoria y configuración
```

## Ejecutar en local

Requiere JDK 17.

```bash
./gradlew build          # compila, ejecuta las 30 pruebas y verifica cobertura >= 80 %
./gradlew bootRun        # levanta el servicio en http://localhost:8080
```

Ejemplo de pedido:

```bash
curl -X POST http://localhost:8080/api/pedidos -H "Content-Type: application/json" \
  -d '{"idCliente":"C-001","items":[{"codigoProducto":"P-001","cantidad":2}],"modalidadDespacho":"ESTANDAR","transportista":"TRANSAGRO","direccionEntrega":"Talca"}'
```

Datos de prueba: clientes `C-001`, `C-002` (activos) y `C-003` (inactivo); productos `P-001`, `P-002`, `P-003`;
modalidades `ESTANDAR`, `EXPRESS`, `RETIRO_EN_BODEGA`; transportistas `TRANSAGRO`, `RUTASUR`.

## Pipeline CI/CD (`.github/workflows/ci-cd.yml`)

| Etapa | Job | Qué hace | Quality Gate |
|---|---|---|---|
| 1 | Compilación y pruebas | `./gradlew build`: compila con JDK 17, pruebas unitarias, de integración (MockMvc) y de arquitectura (ArchUnit) | Falla si una prueba falla, si se rompe una regla de capas o si la cobertura baja de 80 % |
| 2 | Análisis de seguridad | Escanea el JAR con Grype | Falla con vulnerabilidades críticas que tengan corrección |
| 3 | Empaquetado Docker | Imagen multietapa; en `main` se publica en GitHub Container Registry con el SHA del commit | Falla si la imagen no construye |
| 4 | Despliegue a staging | Solo en `main`: levanta el contenedor, espera readiness y ejecuta una prueba de humo de RF-04 | Falla si el servicio no queda listo o la prueba de humo no devuelve `DESPACHO_SOLICITADO` |

En `k8s/deployment.yaml` está el despliegue objetivo en Kubernetes (3 réplicas, probes de liveness/readiness y HPA al 70 % de CPU).
