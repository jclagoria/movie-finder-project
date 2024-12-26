# Utiliza la imagen base de OpenJDK compatible con Java 21
FROM eclipse-temurin:21-jre-alpine

# Establece un directorio de trabajo
WORKDIR /app

# Copia el archivo JAR a la imagen
ARG JAR_FILE
COPY target/${JAR_FILE}.jar app.jar

# Expone el puerto de la aplicación
EXPOSE 8082

# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]
