PL/0 -> Java Bytecode Translator
==============================================

How to build
------------

Having Maven 3 installed in your computer, type:

>>> mvn compile
>>> mvn package

By doing so, you will generate the JAR file in the *target* directory.

How to run
----------

You can run the JAR by typing:

>>> cd target
>>> java -jar pl0compiler-1.0-RELEASE.jar <filename_to_be_translated>
