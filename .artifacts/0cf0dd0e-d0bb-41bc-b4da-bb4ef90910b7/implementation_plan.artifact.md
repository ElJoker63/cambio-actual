# Plan de Optimización para Cambio Actual

Este plan detalla las mejoras estructurales y de rendimiento para la aplicación, enfocándose en la eficiencia de datos, red y arquitectura.

## User Review Required

> [!IMPORTANT]
> Se implementará **Dagger Hilt** como motor de Inyección de Dependencias. Esto cambiará la forma en que se instancian los ViewModels en las pantallas, pasando de `viewModel { ... }` manual a `@HiltViewModel`.

> [!NOTE]
> Se migrará el parseo de JSON de reflexión a **KSP (Kotlin Symbol Processing)** para Moshi, lo que mejorará el rendimiento en tiempo de ejecución.

## Proposed Changes

### 1. Infraestructura y Dependencias

*   **[MODIFY] [libs.versions.toml](file:///D:/_PROJETCS/Github/cambio-actual/gradle/libs.versions.toml)**: Agregar dependencias de Hilt y configurar versiones para KSP.
*   **[MODIFY] [build.gradle.kts (root)](file:///D:/_PROJETCS/Github/cambio-actual/build.gradle.kts)**: Aplicar plugin de Hilt.
*   **[MODIFY] [build.gradle.kts (app)](file:///D:/_PROJETCS/Github/cambio-actual/app/build.gradle.kts)**: Configurar plugins y dependencias de Hilt.
*   **[NEW] CambioActualApp.kt**: Clase Application anotada con `@HiltAndroidApp`.

---

### 2. Capa de Red y Datos (Optimización)

*   **[MODIFY] [NetworkModule.kt](file:///D:/_PROJETCS/Github/cambio-actual/app/cambio-actual/main/java/com/aewaredev/cambioactual/data/api/NetworkModule.kt)**: Refactorizar a un Módulo de Hilt (`@Module`). Garantizar Singletons para Retrofit y OkHttpClient.
*   **[MODIFY] [RateDao.kt](file:///D:/_PROJETCS/Github/cambio-actual/app/cambio-actual/main/java/com/aewaredev/cambioactual/data/local/RateDao.kt)**: Agregar soporte para inserciones masivas (`insertAll`).
*   **[MODIFY] [ExchangeRepositoryImpl.kt](file:///D:/_PROJETCS/Github/cambio-actual/app/cambio-actual/main/java/com/aewaredev/cambioactual/data/repository/ExchangeRepositoryImpl.kt)**:
    *   Usar `@Inject` en el constructor.
    *   Optimizar `refreshRates` para insertar todas las tasas de una sola vez.

---

### 3. Arquitectura y Limpieza

*   **[NEW] UpdateManager.kt**: Extraer la lógica de descarga e instalación de APKs del ViewModel a esta nueva clase.
*   **[MODIFY] ViewModels**: Anotar todos con `@HiltViewModel` y usar `@Inject` en constructores.
*   **[MODIFY] [NavApp.kt](file:///D:/_PROJETCS/Github/cambio-actual/app/cambio-actual/main/java/com/aewaredev/cambioactual/ui/navigation/NavApp.kt)**:
    *   Eliminar instanciación manual de ViewModels.
    *   Simplificar el `NavDisplay` extrayendo componentes de navegación.
*   **[MODIFY] [ConverterScreen.kt](file:///D:/_PROJETCS/Github/cambio-actual/app/cambio-actual/main/java/com/aewaredev/cambioactual/ui/screens/ConverterScreen.kt)**: Descomponer en funciones composables más pequeñas para mejorar la eficiencia de recomposición.

## Verification Plan

### Automated Tests
*   Ejecutar `./gradlew assembleDebug` para verificar la generación de código de Hilt y Room.
*   Verificar que no haya regresiones en el parseo de datos (Moshi KSP).

### Manual Verification
1.  **Inicio de App**: Confirmar que la aplicación inicia sin errores de inyección.
2.  **Sincronización**: Forzar una actualización de tasas y verificar en Logcat que las inserciones masivas funcionan.
3.  **Convertidor**: Verificar que los cálculos siguen siendo correctos tras la refactorización de UI.
4.  **Actualizaciones**: Simular la detección de una nueva versión y verificar que el diálogo de actualización aparece correctamente.
