# dEventer
**Realizado por:**

* Martín Escamilla, José Ignacio

* Ahamad, Achraf

* Estornell Salamanca, Alejandro Antonio

## Idea del proyecto
Planteamos el desarrollo de una aplicación móvil destinada a Android —nativo— en la que los usuarios puedan crear eventos, quedadas o plantes y, a través de la misma, otros usuarios —amigos, conocidos o, incluso, desconocidos— vean esas actividades, dentro de unos parámetros preestablecidos, y se unan a ellas si están interesados.

De este modo, los usuarios serán capaces tanto de ver las actividades más cercanas y afines a las que pueden apuntarse, como subir las propias para que otros se inscriban.

## Objetivos
Uno de los objetivos y retos más importantes al que nos vamos a enfrentar es el diseño y desarrollo de una base de datos NoSQL en la nube que no requiera de una capa intermedia o _web service_ entre la aplicación y los datos. La dificultad que implica no reside tanto en el funcionamiento intrínseco de esta modalidad de almacenamiento, sino en la curva de aprendizaje al haber tratado poco con este tipo de tecnologías.

El usuario ha de iniciar sesión con un proveedor externo —Google— y, a partir de ahí, podrá, como hemos señalado con anterioriodad, visualizar una lista de actividades filtradas según sus preferencias y geolocalización. Ya solo le queda elegir alguna para, posteriormente, si la actividad no tiene límite de particnpantes o no se ha llegado a este, inscribirse.

Así, dispondrá también de otros listados de actividades apuntadas pendientes de realización y actividades en las que ya ha participado, donde tendrá la posbilidad de establecer una valoración/puntuación.

## Tecnologías a utilizar
A la hora de enumerar las tecnologías que utilizaramos, es necesario distinguir dos ámbitos: el de la aplicación móvil que ejecutan los usuarios y el servidor/base de datos que almacena los datos de estos usuarios.

En el primer caso —el de la _app_ móvil, destacan:

* Java SE 7 y algunas funcionalidades de Java SE 8 soportadas como lenguaje de programación en Android.
* Android Studio como IDE con el que desarrollar la aplicación móvil que dispone, además, de integración a través de un _plugin_ con las herramientas de Firebase.
* API 29 —Android 10— de Android, la última versión estable lanzada al mercado.
* API Firebase de Java/Andorid, especialmente las dedicadas a Firebase Authentication y Cloud Firestore.
* Se valorará e intentará seguir, en la medida de lo posible y según su óptima adaptación a las particularidades del proyecto, uno de los siguientes patrones de arquitectura de _software_:
  * Modelo-vista-controlador.
  * Modelo-vista-presentador.
  * Modelo-vista-modelo de vista.
* El apartado visual tiene por objeto seguir las pautas de diseño marcadas por Material Design en la búsqueda de un diseño limpio y agradable a la vista.

En el segundo caso —base de datos— se recurre a:

* Firebase Authentication: mecanismo seguro de identificación de usuarios proporcionado por Firebase que libera a los desarrolladores de administrar y mantener un sistema propio de acceso. Es compatible con el _login_ de proveedores externos como Google, Twitter, Facebook, GitHub...
* Cloud Firestore: es una base de datos NoSQL de Google Cloud —y Firebase— similar a MongoDB basada en documentos —que almacenan pares clave-valor, a la manera de JSON— y colecciones de documentos. Permite la sincronización entre dispositivos, actualización de datos en tiempo real y el almacenamiento local —_offline_— de estos gracias a copias en caché. Asimismo, es fácilmente escalable y cuenta con tecnologías _serverless_, esto es, sin la necesidad de ejecutar y administrar un servidor que actúe de intermediario entre la _app_ y la base de datos.

Otras herramientas útiles a destacar son:

* GitHub: repositorio de código _online_ con control de versiones a través de Git.
* draw.io: programa de diseño de diagramas de flujo, UML, arquitectura de _software_...

## Fases del proyecto
1. Análisis de requisitos.
2. Diseño de la arquitectura de _software_.
3. Diseño del modelo de datos y la lógica de negocio.
4. Diseño de la base de datos NoSQL.
5. Diseño visual de la aplicación móvil.
6. Desarrollo de la aplicación móvil.
7. Puesta en marcha de la base de datos.
8. _Testing_ y QA.

## Bibliografía
* https://www.oracle.com/technetwork/java/javase/overview/index.html
* https://developer.android.com/studio/write/java8-support
* https://developer.android.com/studio
* https://developer.android.com/about/versions/10
* https://developer.android.com/about/versions/10/features
* https://material.io/design
* https://www.tutorialspoint.com/mvc_framework/index.htm
* https://www.tutorialspoint.com/design_pattern/mvc_pattern.htm
* https://medium.com/upday-devs/android-architecture-patterns-part-1-model-view-controller-3baecef5f2b6
* https://medium.com/cr8resume/make-you-hand-dirty-with-mvp-model-view-presenter-eab5b5c16e42
* https://medium.com/upday-devs/android-architecture-patterns-part-2-model-view-presenter-8a6faaae14a5
* https://www.raywenderlich.com/7026-getting-started-with-mvp-model-view-presenter-on-android
* https://medium.com/upday-devs/android-architecture-patterns-part-3-model-view-viewmodel-e7eeee76b73b
* https://cloud.google.com
* https://firebase.google.com
* https://firebase.google.com/docs/reference/android/packages
* https://firebase.google.com/products/auth
* https://firebase.google.com/docs/auth
* https://firebase.google.com/docs/firestore
* https://firebase.google.com/products/firestore
* https://github.com
* https://git-scm.com
* https://drawio-app.com
