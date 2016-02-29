2gis Test Task
============================

*Instructions:*
- Package with maven <br>
- Run with parameter: <br>
Change PATH/TO/MAVEN/REPO/ to your actual path to maven <br>
`java -jar -javaagent:PATH/TO/MAVEN/REPO/.m2/repository/co/paralleluniverse/quasar-core/0.7.2/quasar-core-0.7.2.jar web-service-1.0-fat.jar`<br>

**If path to quasar-core.jar is wrong your log will contain next warning: QUASAR WARNING: Quasar Java Agent isn't running.**

*Note: service will be deployed to localhost:8080*