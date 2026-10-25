# Koltec Events — Gestión móvil de oportunidades y activaciones corporativas

App Android (Kotlin + Jetpack Compose + Material 3) del caso **DSY1105 · Gestión móvil de oportunidades y
activaciones corporativas**. Es un MVP académico: **todos los datos son ficticios** (nombres, correos,
teléfonos, costos y resultados). No se usan RUT, domicilios, correos ni contactos reales.

**Integrantes:** Juan David Motaban - OSVALDO ANTONIO QUIROGA MURCIA
**Trello:** _(enlace al tablero)_

## Arquitectura MVVM

```
model/        Entidades y enums (Opportunity, Usuario, Organizacion, estados, prioridades...)
data/local/   SQLite: AppDatabase (esquema), AppDao (consultas), SeedData (datos ficticios), SessionStore
repository/   AuthRepository, UsuarioRepository, OrganizacionRepository, OportunidadRepository (flujos reactivos)
validation/   Validators, FormValidators, OrgValidator  → reglas puras, sin Android ni Compose
domain/       AlertEngine (alertas), OpportunityFilter (filtros), DateUtils/Hashing
viewmodel/    Estado de cada pantalla (UiState) + lógica; no conocen Compose
ui/           Pantallas Compose, componentes y tema: solo muestran estado y envían eventos
navigation/   AppNav (rutas); sin lógica de negocio
device/       Recursos nativos: cámara, ubicación y lector/generador QR
```

Flujo: `Pantalla (UI) → ViewModel → Repositorio → SQLite`. La UI observa `StateFlow`/`State`; las
validaciones viven en `validation/` y los ViewModels las ejecutan.

## Funcionalidades (según el caso)

- **Autenticación:** inicio de sesión con cuentas ficticias, "recordar sesión", registro de usuario
  (usuario normal o usuario administración con código), perfil y cierre de sesión.
- **Empresas:** registrar empresa o unirse a una existente (cuenta del usuario → *Mi empresa*).
- **Oportunidades:** crear, editar, eliminar, consultar; evento, organización, fechas, horario, comuna/región,
  público objetivo, asistentes, condiciones, costo referencial, prioridad, responsable, potencial y afinidad.
- **Contactos ficticios** por oportunidad (cargo/área, correo, teléfono).
- **Seguimiento:** acciones realizadas, resultado, próxima acción y fecha.
- **Estados** (13) con **historial** de cambios.
- **Resultado de la activación** (asistentes, contactos, interesados, inscripciones, evaluación, aprendizajes).
- **Alertas y recordatorios internos:** eventos próximos, seguimientos próximos y atrasados, propuestas sin
  respuesta, oportunidades sin contacto e información incompleta.
- **Búsqueda y filtros:** nombre, fecha, estado, tipo, región/comuna, prioridad, organización y responsable.
- **Recursos nativos:** cámara (foto del lugar/afiche, app de cámara del sistema) y ubicación aproximada
  (permiso en tiempo de ejecución, opcional; solo sugiere la comuna, no se guarda la posición).
- **Lector QR** (recurso nativo): en *Mi empresa* se escanea el QR de una empresa para unirse a ella, y en la
  pestaña *Oportunidades* se escanea el QR de una oportunidad para abrir su ficha. Cada empresa y cada
  oportunidad puede mostrar su propio QR (botón QR). Formato del contenido: `KOLTEC:EMP:<id>` y `KOLTEC:OPP:<id>`.
  Usa Google Code Scanner (ML Kit; requiere Google Play Services, sin permiso CAMERA) y ZXing para generar la imagen.
- **Animaciones:** transiciones entre pantallas, contadores animados, secciones expandibles, cambio de pestañas.

## Cuentas de prueba

| Usuario | Contraseña | Tipo |
|---|---|---|
| `admin` | `123` | Administración (sin empresa → pasa por *Mi empresa*) |
| `ejecutivo@demo.cl` | `Demo1234` | Usuario normal (empresa 1) |
| `ejecutiva2@demo.cl` | `Demo1234` | Usuario normal (empresa 3) |

Código para registrar un usuario de administración: `KOLTEC-ADMIN`.

## Cómo ejecutar

1. Abrir la carpeta en Android Studio (versión con soporte para AGP 9) y sincronizar Gradle.
2. Ejecutar en un emulador o dispositivo (minSdk 24).
3. Para borrar los datos de prueba: desinstalar la app o "Borrar almacenamiento".
4. El lector QR necesita un emulador con imagen "Google Play"/"Google APIs" o un teléfono con Play Services.
   Para probarlo: abre una empresa u oportunidad, toca el botón QR y escanea esa pantalla con otro teléfono, o
   imprime/guarda el QR y escanéalo con la app (los ids son locales a la base de datos del dispositivo).
5. Pruebas unitarias: `./gradlew testDebugUnitTest`.
5. Para probar la ubicación en el emulador: *Extended controls → Location*.

## Git y planificación

- Commits pequeños y descriptivos (ej.: "Agrega filtros de oportunidades por estado y comuna").
- Tablero Trello con columnas *Por hacer / En curso / Hecho* y tareas asignadas a cada integrante.
