# kDD
**Realizado por:**
* Martín Escamilla, José Ignacio
* Ahamad, Achraf
* Estornell Salamanca, Alejandro Antonio

## Índice
1. Introducción
2. Objetivos del proyecto
3. Análisis de requisitos
4. Especificación de requisitos
   1. Casos de uso
5. Tecnologías utilizadas
   1. Firebase
      1. Firebase Authentication
      2. Firebase Firestore
      3. Firebase Cloud Storage
   2. Java
   3. Android
   4. Material Design
   6. Google Maps Platform
   5. Otros
      1. Android Studio
      2. Git y GitHub
      3. Diagrams
6. Modelo de datos
7. Arquitectura de _software_
8. Patrones de diseño
   1. Patrón MVC
   2. Patrón MVP
   3. Patrón MVVM
   4. Elección del patrón
9. Configuración de Firebase
   1. Configuración general del proyecto
   2. Firebase Authenticator
   3. Firebase Firestore
   4. Firebase Cloud Storage
10. Aplicación móvil
    1. Introducción
    2. Breve estudio visual
    3. _View_
    4. _ViewModel_
    5. _Model_
11. Conclusión
12. Mejoras
13. Bibliografía

## Introducción
Es 2020, han pasado más de 10 años desde la presentación del sistema operativo Android —en 2007, el día 5 de noviembre— y el lanzamiento del primer _smartphone_ en hacer uso de este —HTC Dream, anunciado el 23 de septiembre de 2008 y puesto a la venta un mes más tarde, el 20 de octubre de 2008—. Desde entonces, sin apenas competencia con la excepción de Apple y su incursión en el terreno móvil gracias al iPhone y su sistema operativo iOS, Android es el dominador absoluto del mercado. Para ello una muestra:

* En mayo de 2019 superó la barrera de 2.500 millones de dispositivos activos mensuales.

![StatCounter Global Stats | Operating System Market Share Worldwide](../images/os-market-share-ww.png)

* En los últimos doce meses, contaba con una cuota de mercado del 38.61 % entre todos los sistemas operativos —Windows, iOS, OS X y Linux—; ocupando la primera posición.

![StatCounter Global Stats | Mobile Operating System Market Share Worldwide](../images/mobile-os-market-share-ww.png)

* Si atendemos exclusivamente al mercado móvil, este liderazgo aumenta considerablemente sobre el resto hasta alcanzar un 72.26 % en marzo de 2020 por el 27.03 % de iOS —el resto de opciones son marginales, inferiores a 0.1 %—.

En otro orden, dado que, como ha de suponerse, nos centraremos en el sistema operativo Android, conviene tener en cuenta la distribución de versiones de la plataforma en el mercado. O lo que es lo mismo, el porcentaje de uso de cada una de las API lanzadas cada cierto tiempo —anualmente— para, con esta información decidir a cuál de estas —o, más concretamente, desde cuál—, damos soporte.

El panel de distribución oficial suministrado por el canal de Android Developers data del 7 de mayo de 2019 y, aunque útil, no coincide con la situación real en marzo de 2020 —momento en el que comenzamos con el proyecto—. Con todo, los datos, como primera toma de contacto, pueden servir de impresión inicial. Es ahí donde se observa lo siguiente:

![Distribution dashboard](../images/android-version.png)

| Version | Codename | API | Distribution |
| ------- | -------- | --- | ------------ |
| 2.3.3 - 2.3.7 | Gingerbread | 10 | 0.3 % |
| 4.0.3 - 4.0.4	| Ice Cream Sandwich | 15 | 0.3 % |
| 4.1.x	| Jelly Bean | 16 | 1.2 % |
| 4.2.x	|   | 17 | 1.5 % |
| 4.3 |   | 18 | 0.5 % |
| 4.4 | KitKat | 19 | 6.9 % |
| 5.0 | Lollipop | 21 | 3.0 % |
| 5.1 |   | 22 | 11.5 % |
| 6.0 | Marshmallow | 23 | 16.9 % |
| 7.0 | Nougat |24 | 11.4 % |
| 7.1 |   | 25 | 7.8 % |
| 8.0 | Oreo | 26 | 12.9 % |
| 8.1 |   | 27 | 15.4 % |
| 9 | Pie | 28 | 10.4 % |

El problema de la tabla superior es su falta de actualización. Es por ello que para conocer mejor el _estado real de las cosas_ debemos recurrir a otras herramientas de visualización de datos en _tiempo real_ —mensualidad entendida como tiempo real—. Ya la hemos utilizado previamente, en la comparación de cuotas de mercado. Se trata de StatCounter, una plataforma de análisis de tráfico _web_ —entre otros— que nos proporciona justo la información que necesitamos actualmente sobre la distribución de Android a nivel mundial. La siguiente imagen, sacada de esta herramienta, vislumbra un panorama bastante distinto:

![StatCounter Global Stats | Mobile & Tablet Android Version Market Share Worldwide](../images/android-version-ww.png)

* Por un lado, aparece en escena una nueva versión estable de la API de Android: Android 10 o API 29. Hay que tener en cuenta asimismo, según informa la página de desarrollo oficial de Android, que está en camino Android 11 —API 30—; en principio para este mismo año, 2020 y ya han distribuido las Developer Preview o, lo que es lo mismo, versiones que podríamos entender por _alpha_ o _beta_ para los desarrolladores.
* Por el otro lado, aun en estancamiento y descenso desde diciembre de 2019, Android 9 Pie copa el 38.57 % del mercado, Android 10 consigue situarse en segunda posición gracias a su 12.45 % y, a partir de ahí, la tercera posición queda para Android 8.1 Oreo —11.82 %—. En total, la suma de cuotas de Android 8.0 Oreo en adelante constituye el 70.46 % de los dispositivos con Android instalados y actualmente en uso.

Será vital, en su momento —y más si nos lo planteamos como una situación de trabajo real—, ponderar las cifras anteriores. Especialmente en lo relativo al SDK mínimo con el que trabajemos —versión más baja de Android a la que damos soporte o en la que puede ser instalada y utilizada nuestra aplicación— y el SDK _target_ u objetivo; aquella para la que desarrollamos y compilamos haciendo uso de sus librerías y con la que es totalmente compatible.

No escapa a nadie que el uso de aplicaciones móviles está —y lleva— en auge desde hace bastante tiempo, en tendencia ascendente. Para todo tipo de sectores y acciones que nunca habríamos imaginado. Pero la innovación siempre encuentra su camino y si no, lo hace.

Ya lo señaló el informe _Sociedad Digital en España 2018_ publicado el año pasado por la Fundación Telefónica: el _smartphone_ es el dispositivo preferido por el 91.9 % de la población española para cualquier tipo de uso.

De dicho informe pueden extrarse más datos relevantes que vienen a consolidar la afirmación que hicimos antes: la descarga y uso de aplicaciones móviles no para de crecer. Entre estos encontramos:

* En 2017 se descargaron 178.100 millones de _apps_. Cifra que aumentó hasta los 258.200 millones de 2019 y que se traduce en una subida del 44 %.
* Los usuarios valoran en una aplicación —en orden descendiente—:
  1. Cubrir una necesidad: 72 %.
  2. Ausencia de publicidad intrusiva: 57 %.
  3. Consumo bajo de datos: 46 %.
* El 32 % de la población dedica más de 20 horas semanales a _apps_, el 13 % entre 16 y 20 horas, el 17 % se mantiene en una franja que va desde las 11 horas hasta las 16, el 25 % entre 6 y 10 horas y, por último, el 13 % restante llega a las 5 horas.
* El dispositivo preferido para la mensajería instantanéa es el _smartphone_ por un 86.7 %. Esta predominancia se observa también en el acceso a redes sociales; sigue siendo el dispositivo predilecto con una cifra cercana al 60 %.

Esta breve presentación de datos no hacen sino confirmar la premisa de la que partíamos: el uso de las aplicaciones continúa su senda de crecimiento; y no parece que vaya a verse mermada en el corto plazo.

La introducción tenía el propósito de servir de preámbulo a nuestro proyecto y ofrecer unas pinceladas iniciales que dieran, más o menos, cuenta, a partir de la información presentada, de la senda que pretendemos tomar para el módulo profesional de Proyecto. De alguna forma hemos de defenderlo, y creemos que se asienta sobre una base de mercado bastante estable, robusta y con posibilidades de éxito.

Nuestro objetivo es el desarrollo de una aplicación móvil para el sistema operativo Android en la que los usuarios tengan la oportunidad de crear eventos —lo que comúnmente se conoce como _quedadas_— de cualquier tipo y, a través de esta, otros usuarios —como amigos, conocidos o incluso desconocidos— vean estas actividades, dentro de unos parámetros preestablecidos, y se unan a ellas si están interesados.

De este modo, los usuarios registrados que utilicen la _app_ serán capaces tanto de ver las actividades más cercanas y afines a las que pueden apuntarse, como de subir las propias para que otros se inscriban.

Todo ello aderezado de un conjunto de características adicionales —como _chat_ entre los miembros inscritos a una actividad— que detallaremos en profundidad durante el desarrollo de este documento; en futuros apartados.

El deseo y la decisión de llevar a cabo una aplicación de este tipo se tomó a principios de marzo —con lo lejos, aparentemente, que queda ya en el momento de escribir esto—. La decisión se basaba, entre otros, en dos pilares fundamentales.

Uno, no encontrábamos una aplicación en `Play Store` que supliera esta necesidad —propia para el caso— de forma específica y según nuestros requisitos. Sí, es cierto que existen gigantes como Meetup o Eventbrite. El problema de estas es, por un lado, la orientación de la primera al hecho de conocer gente nueva y no tanto a la organización de planes con conocidos y, por el otro, el carácter profesional o corporativo asociado a Eventbrite —más en la línea del _coworking_, emprendimiento y desarrollo de _startups_—.

Dos, Fever, una propuesta potente, asentada, y bastante similar en apariencia a la nuestra, se posiciona del lado del promotor de la actividad, de empresas cuya actividad consiste, precisamente, en eso, en crear y organizar planes, normalmente con un coste asociado. A fin de cuentas, no deja de ser una plataforma en la que grandes cadenas de restauración, teatros, conciertos, museos... publican las actividades que organizan —aun sin depender de Fever y disponibles por otros medios— para que, gracias precisamente a Fever, el usuario final o cliente de estos se decida por una y compre el tique. No deja de ser similar a Ticketmaster y alternativas parecidas.

Nuestra idea, por el contrario, no podía estar más alejada. Queremos desarrollar una aplicación en las antípodas de las anteriores, con un toque desenfadado, alejada de toda pretensión de explotación comercial por parte de posibles colaboradores futuros que quieran publicar en ella sus productos —eventos o actividades— a aquellos que la usen.

Debe entenderse como un lugar en el que concretar aquel partido de fútbol que siempre se intenta jugar entre amigos y nunca sale; ese _ya si eso quedamos_ tan común con compañeros o conocidos a los que nos encontramos tras demasiado tiempo sin saber de ellos; o, por qué no, para organizar la salida del viernes por la noche con los compañeros de clase. El contenido ha de estar generado, en todo momento, por usuarios particulares e individuales. Las empresas que pudieran estar asociadas a los lugares en los que van a tener lugar esos eventos no son el tipo de usuario que esperamos en nuestra aplicación.

Como ya se señaló previamente, esta decisión se tomó en los primeros días de marzo, cuando la coyuntura todavía era propicia y no nos había alcanzado la crisis asociada al COVID-19. Tras el estado de alarma decretado por el Gobierno de España, las restricciónes a la libre circulación y reunión de personas y la lenta recuperación y vuelta a la normalidad que se prevé, especialmente en materia sociocultural, pudiera parecer el momento menos adecuado para un desarrollo de esta naturaleza. No podemos negar la mayor, lo sabemos, no es la situación adecuada para que prospere. No obstante, confiamos en que tras la recuperación, con el paso del tiempo, fuera capaz de erigirse en una opción válida para la realización de actividades con un fuerte componente social y, sobre todo, sin grandes pretensiones; planes sencillos que surgen en el día a día y no cuestan nada o casi nada.

## Objetivos del proyecto

## Bibliografía
* [Wikipedia | Android (operating system) History](https://en.wikipedia.org/wiki/Android_(operating_system)#History)
* [Open Handset Alliance | Industry Leaders Announce Open Platform for Mobile Devices](http://www.openhandsetalliance.com/press_110507.html)
* [TechCrunch | Breaking: Google Announces Android and Open Handset Alliance](https://techcrunch.com/2007/11/05/breaking-google-announces-android-and-open-handset-alliance/)
* [Google Official Blog | Where's my Gphone?](https://googleblog.blogspot.com/2007/11/wheres-my-gphone.html)
* [Gizmodo | T-Mobile G1: Full Details of the HTC Dream Android Phone](https://gizmodo.com/t-mobile-g1-full-details-of-the-htc-dream-android-phon-5053264)
* [Wikipedia | HTC Dream Release](https://en.wikipedia.org/wiki/HTC_Dream#Release)
* [Wikipedia | Android (operating system) Market share](https://en.wikipedia.org/wiki/Android_(operating_system)#Market_share)
* [Official Android Twitter | 2.5 Billion Monthly Active Devices](https://twitter.com/Android/status/1125822326183014401)
* [Wikipedia | Usage share of operating systems](https://en.wikipedia.org/wiki/Usage_share_of_operating_systems)
* [StatCounter Global Stats | Operating System Market Share Worldwide](https://gs.statcounter.com/os-market-share)
* [StatCounter Global Stats | Mobile Operating System Market Share Worldwide](https://gs.statcounter.com/os-market-share/mobile/worldwide)
* [Android Developers | Distribution dashboard](https://developer.android.com/about/dashboards)
* [StatCounter Global Stats | Mobile & Tablet Android Version Market Share Worldwide](https://gs.statcounter.com/os-version-market-share/android/mobile-tablet/worldwide)
* [StatCounter](https://statcounter.com/)
* [Wikipedia | StatCounter](https://en.wikipedia.org/wiki/StatCounter)
* [Wikipedia | Android (operating system) Platform information](https://en.wikipedia.org/wiki/Android_(operating_system)#Platform_information)
* [Wikipedia | Android version history](https://en.wikipedia.org/wiki/Android_version_history)
* [Official Android 8.0 Oreo](https://www.android.com/versions/oreo-8-0/)
* [Android Developers | Android Oreo Overview](https://developer.android.com/about/versions/oreo)
* [Official Android 9.0 Pie](https://www.android.com/versions/pie-9-0/)
* [Android Developers | Android 9 Pie Overview](https://developer.android.com/about/versions/pie)
* [Official Android 10.0](https://www.android.com/android-10/)
* [Android Developers | Android 10 Home](https://developer.android.com/about/versions/10)
* [Android Developers | Android 11 Developer Preview Home](https://developer.android.com/preview)
* [Android Developers | Android 11 Developer Preview Release Notes](https://developer.android.com/preview/release-notes)
* [Fundación Telefónica | Sociedad Digital en España 2018](https://www.fundaciontelefonica.com/cultura-digital/publicaciones/sociedad-digital-en-espana-2018/655/)
* [Wikipedia | Meetup](https://en.wikipedia.org/wiki/Meetup)
* [Meetup](https://www.meetup.com/es-ES/)
* [Wikipedia | Eventbrite](https://en.wikipedia.org/wiki/Eventbrite)
* [Eventbrite](https://www.eventbrite.es/)
* [Fever](https://feverup.com/madrid)
* [Wikipedia | Ticketmaster](https://en.wikipedia.org/wiki/Ticketmaster)
* [Ticketmaster](https://www.ticketmaster.es/)

---
> Portions of this page are reproduced from work created and shared by the Android Open Source Project and used according to terms described in the Creative Commons 2.5 Attribution License.

---
> Portions of this page are modifications based on work created and shared by the Android Open Source Project and used according to terms described in the Creative Commons 2.5 Attribution License.
