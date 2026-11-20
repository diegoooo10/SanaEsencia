# 🌿 SanaEsencia — Aplicación Android

Aplicación móvil para consultar prestaciones de bienestar, explorar profesionales, reservar atenciones y gestionar el perfil de usuario. Este repositorio contiene **exclusivamente el frontend Android** de SanaEsencia; los microservicios y sus bases de datos se mantienen en proyectos independientes.

> **Estado:** MVP funcional en etapa de pruebas. La integración de pagos utiliza **Webpay Plus TEST**; no se realizan cobros reales.

## 📱 Funcionalidades

### Cuenta y perfil
- Registro de usuarios con validación de datos.
- Inicio y cierre de sesión mediante autenticación JWT.
- Consulta y edición de información del perfil.
- Recuperación de contraseña por correo electrónico.
- Apertura de enlaces de restablecimiento mediante Android App Links.

### Reservas
- Consulta de especialidades, prestaciones y profesionales.
- Selección de modalidad de atención **presencial u online**.
- Consulta de disponibilidad y selección de horarios.
- Creación de reservas y visualización de próximas atenciones e historial.
- Consulta del detalle de una reserva.
- Cancelación de reservas confirmadas cuando se cumple la política de anticipación.
- Visualización de estados de reserva, incluida la cancelación.

### Pagos
- Inicio del proceso de pago mediante Webpay Plus en ambiente de integración.
- Retorno a la aplicación después del flujo de pago.
- Visualización del resultado de la operación y actualización de la reserva.
- Manejo de procesos de pago anulados.

La validación definitiva de pagos, disponibilidad y reglas de cancelación se realiza en el **backend**, no exclusivamente en Android.

## 🛠️ Tecnologías

| Tecnología | Uso |
| --- | --- |
| **Kotlin** | Desarrollo de la aplicación |
| **Jetpack Compose** | Interfaz declarativa |
| **Material 3** | Componentes de interfaz |
| **MVVM** | Separación entre presentación y acceso a datos |
| **ViewModel y StateFlow** | Manejo de estados de pantalla |
| **Retrofit** | Consumo de API REST |
| **Coroutines** | Operaciones asíncronas |
| **Android App Links** | Enlaces de recuperación de contraseña |
| **Gradle** | Compilación y dependencias |

## 🏗️ Arquitectura

La aplicación utiliza el patrón **MVVM (Model–View–ViewModel)**. Las pantallas Compose presentan el estado expuesto por sus ViewModels; los repositorios encapsulan el acceso a datos y Retrofit realiza las solicitudes HTTP al API Gateway.

```text
┌───────────────────────────────┐
│ Pantallas Jetpack Compose     │
│ Inicio · Reservas · Perfil    │
└──────────────┬────────────────┘
               │
┌──────────────▼────────────────┐
│ ViewModels / StateFlow        │
└──────────────┬────────────────┘
               │
┌──────────────▼────────────────┐
│ Repositorios                  │
└──────────────┬────────────────┘
               │
┌──────────────▼────────────────┐
│ Retrofit / API REST           │
└──────────────┬────────────────┘
               │ HTTPS
┌──────────────▼────────────────┐
│ API Gateway (backend remoto)  │
└───────────────────────────────┘
```

### Organización principal

```text
app/
└── src/main/
    ├── java/com/example/sanaescencia/
    │   ├── data/
    │   │   ├── model/
    │   │   ├── remote/
    │   │   └── repository/
    │   └── ui/
    │       ├── components/
    │       ├── screens/
    │       └── viewmodel/
    ├── res/
    └── AndroidManifest.xml
```

La estructura puede evolucionar conforme avance el desarrollo.

## 🧭 Navegación

La navegación principal contiene tres apartados:

| Sección | Descripción |
| --- | --- |
| **Inicio** | Acceso a la información y funcionalidades principales |
| **Reservas** | Próximas reservas, historial, creación y detalle |
| **Perfil** | Datos personales y opciones de cuenta |

El módulo de tienda o esencias florales **no forma parte del alcance actual del MVP**.

## 🔌 Conexión con el backend

SanaEsencia es un **cliente Android**. La aplicación no incorpora MySQL ni ejecuta microservicios en el teléfono. Las operaciones que requieren persistencia se envían a un API Gateway remoto mediante Retrofit.

El backend gestiona, entre otras responsabilidades:
- Autenticación y autorización.
- Usuarios y perfiles.
- Especialidades, prestaciones y profesionales.
- Disponibilidad y reservas.
- Transacciones de Webpay Plus.

La URL base utilizada por Retrofit debe apuntar al **API Gateway accesible desde el dispositivo**. No utilices `localhost` para conectarte a un servidor remoto desde un teléfono físico.

> **Nota de seguridad:** evita publicar credenciales, tokens, contraseñas, archivos de claves privadas o datos reales de usuarios en el repositorio.

## 🚀 Requisitos y ejecución

### Requisitos
- Android Studio con el SDK requerido por el proyecto.
- JDK compatible con la configuración de Gradle.
- Emulador Android o dispositivo físico.
- Acceso a una instancia operativa del backend para probar funcionalidades conectadas.

### Clonar el repositorio

```bash
git clone https://github.com/diegoooo10/SanaEsencia.git
cd SanaEsencia
```

### Ejecutar
1. Abre el proyecto en **Android Studio**.
2. Espera a que termine la sincronización de Gradle.
3. Revisa que la configuración de Retrofit apunte al entorno de backend deseado.
4. Selecciona un emulador o dispositivo Android.
5. Ejecuta la aplicación con **Run**.

## 📦 Generar APK de pruebas

Desde Android Studio:

**Build → Build Bundle(s) / APK(s) → Build APK(s)**

O desde la terminal del proyecto:

**Windows (PowerShell):**
```powershell
.\gradlew.bat assembleDebug
```

**Linux/macOS:**
```bash
./gradlew assembleDebug
```

El archivo generado se encuentra normalmente en:

```text
app/build/outputs/apk/debug/app-debug.apk
```

La APK **Debug** está destinada a pruebas. Puede mostrar advertencias de instalación por tratarse de una aplicación distribuida fuera de Google Play.

## 🧪 Pruebas funcionales sugeridas

- [ ] Registrar una cuenta nueva.
- [ ] Iniciar sesión con credenciales correctas.
- [ ] Comprobar el mensaje ante credenciales incorrectas.
- [ ] Consultar y editar el perfil.
- [ ] Solicitar recuperación de contraseña y abrir el enlace recibido.
- [ ] Explorar especialidades, prestaciones y profesionales.
- [ ] Seleccionar modalidad y horario disponible.
- [ ] Iniciar una reserva y completar un pago aprobado de prueba.
- [ ] Verificar que la reserva figure como confirmada.
- [ ] Anular un proceso de pago y comprobar el retorno a la aplicación.
- [ ] Cancelar una reserva permitida y verificar su estado.
- [ ] Comprobar la restricción de cancelación con menos de 12 horas de anticipación.
- [ ] Probar la aplicación usando datos móviles y diferentes dispositivos.

### Webpay Plus TEST

La integración actual usa el **ambiente de pruebas** de Transbank. Utiliza únicamente las tarjetas y credenciales publicadas en su documentación de integración; **no ingreses tarjetas reales**.

Documentación: https://www.transbankdevelopers.cl/

## 🔐 Seguridad y reglas de negocio

- Las solicitudes autenticadas utilizan JWT.
- Las contraseñas no se validan ni almacenan como texto plano en la aplicación.
- La recuperación de contraseña utiliza enlaces con vigencia limitada, validados por el backend.
- La restricción de cancelación de reservas confirmadas es de **12 horas de anticipación** y se comprueba en el servidor.
- Las reservas canceladas conservan su historial y liberan su horario cuando corresponde.
- Los resultados de pago se verifican en el backend antes de confirmar una reserva.

## 📌 Estado y alcance

El proyecto es un **MVP en validación funcional**. Su objetivo es demostrar el flujo completo entre la aplicación Android y los servicios remotos: registro, autenticación, consulta de prestaciones, reserva, pago de prueba y administración de reservas.

### Posibles mejoras futuras
- Ajustes de experiencia de usuario y accesibilidad.
- Notificaciones y recordatorios.
- Mayor cobertura de pruebas automatizadas.
- Publicación de una versión Release.
- Integración con plataformas externas de gestión, sujeta a evaluación.
- Funcionalidades adicionales según los requerimientos del centro.

## 📸 Capturas de pantalla

Se pueden incorporar capturas de **Inicio de sesión**, **Inicio**, **Reservas** y **Perfil** cuando estén listas para publicación. Evita incluir datos personales o tokens visibles.

## 👨‍💻 Sobre el proyecto

Proyecto académico y de demostración funcional, desarrollado para aplicar conocimientos de Android, Kotlin, arquitectura MVVM, consumo de API REST e integración con servicios externos.

**Repositorio Android:** https://github.com/diegoooo10/SanaEsencia

---

**SanaEsencia · Aplicación Android para la gestión de reservas de bienestar.**
