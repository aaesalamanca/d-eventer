# Colaborar en dEventer

Como dirían los ingleses, _first things first_. Muchas gracias por tomarte el tiempo de colaborar con nosotros.

Lo que viene a continuación es un conjunto de pautas que debes seguir para colaborar con el desarrollo de dEventer. Son, como dice antes, pautas, no reglas estrictas. Así que siéntete libre de proponer cambios a este documento a través de un _pull request_.

## Índice

1. [Estructura del proyecto](#estructura-del-proyecto)
2. [Generación de documentos](#generación-de-documentos)
3. [Flujo de trabajo](#flujo-de-trabajo)
4. [Guías de estilo](#guías-de-estilo)
   1. [Commit en Git](#commit-en-git)
   2. [Java](#java)
   3. [XML](#xml)
   4. [Documentación](#documentación)

## Estructura del proyecto

* En la raíz del repositorio se encuentran todos los documentos útiles para la plataforma `GitHub` como `README.md` o este que estás leyendo.
* `dEventer/` contiene el proyecto de `Android Studio`.
* `docs/`contiene la base de todos los documentos que han de ser entregados a los profesores. Son —aparecen en orden de entrega—:
  - [x] `Anteproyecto.md` Fecha de entrega: 16/03/2020 — Entregado: 18/03/2020.
  - [ ] `Memoria.md` Fecha de entrega: XX/06/2020 — Entregado: XX/06/2020.
  - [ ] `Presentacion.md` Fecha de entrega: XX/06/2020 — Entregado: XX/06/2020.
  - [ ] `Libreto.md` Fecha de entrega: XX/06/2020 — Entregado: XX/06/2020.
  - [ ] **Proyecto**. Fecha de entrega: XX/06/2020 — Entregado: XX/06/2020.
* `images/` contiene las imágenes utilizadas en los documentos.

## Generación de documentos

* Los documentos utilizan el lenguaje de marcado ligero `markdown` para dar formato sencillo al texto. La idea es que sirvan de raíz o base de la que partir siguiendo este flujo:
  1. Documento inicial en `markdown`.
  2. Aplicar formato final en un procesador de texto como `Google Docs`, `Microsoft Word` o `LifreOffice Writer`; o _software_ destinado a la creación de presentaciones como `Microsoft PowerPoint`, `LibreOffice Impress` o `Google Slides`.
  3. Generar documento final en `.pdf` que será entregado a los profesores.
* `Memoria.md`sirve como raíz de `Presentacion.md`y `Libreto.md`.

## Flujo de trabajo

Para el flujo de trabajo con las ramas —_branches_— y _pull requests_ utilizamos el flujo de trabajo básico definido por GitHub: [GitHub flow](https://help.github.com/en/github/collaborating-with-issues-and-pull-requests/github-flow).

[Understanding the GitHub flow](https://guides.github.com/introduction/flow/) puede servirte para entender el funcionamiento de esta manera de trabajar con repositorios.

La rama `master` está protegida, todo _merge_ a esta rama debe hacerse previo _pull request_ revistado y aprobado. Así, para cada _feature_ se crea una rama nueva con un nombre indicativo como `add-feature` y, posteriormente, cuando esté desarrollada la característica, se solicita el _pull_ — `pull request`—. Tras el _merge_ se elimina la rama; también se elimina si, por algún motivo, no se aprueba el _pull request_ ni se decide seguir con este.

## Guías de estilo

### Commit en Git

* Usa verbos para indicar acciones en tiempo presente: `Añadir característica` y no `Característica añadida`.
* Usa el modo imperativo.
* Limita la primera línea —título— a 72 caracteres o menos.
* Referencia los _issues_ y _pull requests_ después de la primera línea.
* Es recomendable empezar el mensaje de _commit_ con alguno de los siguientes _emoji_:
  * :file_folder: `:file_folder:` al aplicar cambios generales al repositorio.
  * :iphone: `:iphone:` al aplicar cambios generales a la aplicación como nuevas funcionalidades.
  * :art: `:art:`al añadir imágenes, elementos visuales o mejorar el formato/estructura del código.
  * :racehorse: `:racehorse:` al mejorar el rendimiento.
  * :bug: `:bug:` al arreglar un _bug_ —error—.
  * :fire: `:fire:` al eliminar código o archivos.
  * :memo: `:memo:` al escribir o modificar documentos.

### Java

Puedes tomar como referencia [Java Code Conventions - Oracle](https://www.oracle.com/technetwork/java/codeconvtoc-136057.html) o las [guías de estilo de Google](https://github.com/google/styleguide), concretamente [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html). A pesar de ofrecer las dos, preferimos la última.

* Recuerda formatear el código con las herramientas proporcionadas por `Android Studio`. Un atajo para formatear todo el documentos es `Ctrl` + `Alt` + `L` o **Code > Reformat Code**.
* Usa nombres descriptivos y para los elementos de la interfaz sigue estas indicaciones:
  * Añade un prefijo que resuma el elemento. Por ejemplo, `btn` si se trata de un `Button`, `tv` si es un `TextView`, `et`para los `EditText`. Lo mismo para los _layouts_ —`llyt` es `LinearLayout` y `clyt` es `ConstraintLayout`—, _fragments_ —`frg`—, etc.
  * A continuación del prefijo usa un identificador asociado con la función, acción o contenido de ese elemento. Si un `Button` envía texto, quedaría como `btnEnviarTexto`; o si es la `Activity` donde el usuario inicia sesión, usa `ActivityIniciarSesion` —este es un nombre de clase, no de instancia—.
* También recomendamos el siguiente patrón para los _arrays_ y colecciones de datos:
  * El prefijo está compuesto por el tipo de colección: `a` en _arrays_, `al` en `ArrayList`, `tm` en `TreeMap`...
  * Al prefijo se le añade el nombre de la clase o tipo primitivo que contiene la colección —en singular—. Un _array_ de `int` es `aInt` y un `TreeMap` de `Usuario` es `tmUsuario`.

### XML

No hay ninguna indicación especial de momento. Formatea el código en `Android Studio` con el atajo `Ctrl` + `Alt` + `L` o **Code > Reformat Code**

### Documentación

Como se indica anteriormente, los documentos están escritos en `markdown`, concretamente en su especificación de GitHub. A continuación se listan las páginas de referencia para escribir con este lenguaje de marcado ligero:

* [GitHub Flavored Markdown Spec](https://github.github.com/gfm)
* [Mastering Markdown](https://guides.github.com/features/mastering-markdown)
* [Writing on GitHub](https://help.github.com/en/github/writing-on-github)

---

Este documento está basado en [Contributing to Atom](https://github.com/atom/atom/blob/master/CONTRIBUTING.md).
