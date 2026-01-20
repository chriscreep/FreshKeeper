# FreshKeeper App

> **Tu asistente inteligente para la gestión de alimentos y reducción de desperdicios.**

![Android](https://img.shields.io/badge/Platform-Android-3DDC84?style=flat&logo=android&logoColor=white)
![Kotlin](https://img.shields.io/badge/Language-Kotlin-7F52FF?style=flat&logo=kotlin&logoColor=white)
![Firebase](https://img.shields.io/badge/Backend-Firebase-FFCA28?style=flat&logo=firebase&logoColor=black)

## Descripción

**FreshKeeper** es una aplicación nativa de Android diseñada para ayudar a los usuarios a gestionar la fecha de vencimiento de sus alimentos. El objetivo principal es reducir el desperdicio de comida en el hogar mediante recordatorios visuales y consejos de almacenamiento adecuados.

El proyecto integra autenticación de usuarios y almacenamiento en la nube en tiempo real, ofreciendo una experiencia fluida y personalizada.

---

## Características Principales

* **Gestión de Inventario:** CRUD completo (Crear, Leer, Actualizar, Eliminar) de productos alimenticios.
* **Sistema de Alertas:** Indicadores visuales y textuales para productos próximos a vencer (ej. conteo destacado en naranja).
* **Consejos Inteligentes:** Carrusel interactivo (`ViewPager2`) con recomendaciones de conservación de alimentos (ej. cómo guardar lácteos o verduras).
* **Autenticación Segura:** Login y Registro de usuarios mediante **Firebase Auth**.
* **Sincronización en la Nube:** Persistencia de datos en tiempo real con **Firestore**.

---

## Stack Tecnológico

Este proyecto demuestra el uso de tecnologías modernas de desarrollo Android:

* **Lenguaje:** Kotlin
* **Backend as a Service (BaaS):** Firebase (Firestore Database, Authentication)
* **Arquitectura:** Monolitica adaptada a Android.
* **Componentes de UI:**
    * `RecyclerView` para listados eficientes.
    * `ViewPager2` & `TabLayout` para navegación por pestañas y carruseles.
    * `CardView` para diseño de tarjetas (Material Design).
    * `ConstraintLayout` & `LinearLayout` para diseños responsivos.


## Instalación y Configuración

Para correr este proyecto localmente, sigue estos pasos:

1. **Clonar el repositorio:**

   ```bash
   git clone [https://github.com/chriscreep/FreshKeeper.git)

   ### Luego Configurar Firebase <--

Este proyecto requiere un archivo `google-services.json` para conectar con los servicios de Google.

1. Crea un proyecto nuevo en [Firebase Console](https://console.firebase.google.com/).
2. Habilita **Authentication** (selecciona el método *Email/Password*).
3. Habilita **Firestore Database** (comienza en modo de prueba).
4. Descarga tu archivo `google-services.json` desde la configuración del proyecto y colócalo dentro de la carpeta `/app`.

### Compilar

1. Abre el proyecto en **Android Studio**.
2. Sincroniza los archivos Gradle (clic en *Sync Now* o el icono de elefante).
3. Ejecuta la app en un emulador o dispositivo físico presionando **Run** (▶).
