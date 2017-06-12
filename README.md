# CesRssReader
Simple RSS Feed Reader

Developed ad hoc for a job interview:

Librerías:
==========

Se utilizan librerias estandar de Google como recyclerview para la lista de elementos, etc.

Otras librerias serían:

+ com.github.bumptech.glide : para carga y caché de imágenes desde Internet
+ org.jsoup:jsoup : traduce etiquetas HTML a texto para poder otener información de los feeds
+ com.squareup.sqlbrite:sqlbrite y io.reactivex:rxandroid : para mayor agilidad en el acceso a una base de datos SQLite, usando RxJava

Patrones:
=========

+ Una aproximación a MVP fue necesario cuando la clase principal empezaba a crecer, de modo que podamos separar la vista de la forma de presentarla
+ Composición...
+ ...
 
