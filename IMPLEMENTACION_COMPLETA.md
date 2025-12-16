# Sistema de Gestión Vía Email - Implementación Completa

## 📋 Resumen del Proyecto

Sistema de gestión de transporte Trans Comarapa que permite ejecutar operaciones CRUD mediante comandos enviados por correo electrónico. Desarrollado con Spring Boot 3.2.1 y conectado a la misma base de datos PostgreSQL del sistema Laravel existente.

## 🎯 Características Principales

- ✅ **Procesamiento automático de emails** cada 60 segundos
- ✅ **Validación de permisos** basada en roles (Admin, Secretaria, Conductor, Cliente)
- ✅ **Compatibilidad con Laravel** usando BCrypt para passwords
- ✅ **Auditoría completa** de comandos ejecutados
- ✅ **Respuestas en texto plano** legibles por humanos
- ✅ **Manejo robusto de errores** con mensajes descriptivos

## 📁 Estructura del Proyecto

```
Sistema_ViaMail/
├── src/main/java/com/grupo04sa/sistema_via_mail/
│   ├── model/              # 9 entidades JPA
│   │   ├── Usuario.java
│   │   ├── Vehiculo.java
│   │   ├── Ruta.java
│   │   ├── Viaje.java
│   │   ├── Boleto.java
│   │   ├── Encomienda.java
│   │   ├── Venta.java
│   │   ├── PagoVenta.java
│   │   └── EmailLog.java
│   │
│   ├── repository/         # 9 repositorios JPA
│   │   └── ...Repository.java
│   │
│   ├── service/           # Servicios de lógica de negocio
│   │   ├── CommandParserService.java      # Parseo de comandos
│   │   ├── EmailService.java              # Lectura/envío emails
│   │   ├── CommandValidatorService.java   # Validación permisos
│   │   ├── CommandExecutorService.java    # Ejecución comandos
│   │   ├── UsuarioService.java            # CRUD usuarios
│   │   ├── BoletoService.java             # Venta boletos
│   │   ├── EncomiendaService.java         # Registro encomiendas
│   │   └── EmailLogService.java           # Auditoría
│   │
│   ├── scheduler/
│   │   └── EmailScheduler.java            # Polling automático
│   │
│   ├── dto/               # Data Transfer Objects
│   │   ├── CommandRequest.java
│   │   └── CommandResponse.java
│   │
│   ├── util/              # Utilidades
│   │   ├── CommandValidator.java
│   │   └── ResponseFormatter.java
│   │
│   ├── exception/         # Excepciones personalizadas
│   │   ├── CommandException.java
│   │   ├── ValidationException.java
│   │   ├── UnauthorizedException.java
│   │   └── EntityNotFoundException.java
│   │
│   └── SistemaViaMailApplication.java     # Clase principal
│
├── src/main/resources/
│   └── application.properties             # Configuración
│
└── pom.xml                                # Dependencias Maven
```

## 🔧 Configuración

### Base de Datos (application.properties)
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/transcomarapa
spring.datasource.username=postgres
spring.datasource.password=tu_password
spring.jpa.hibernate.ddl-auto=validate
```

### Correo Electrónico
```properties
# Entrada (POP3)
mail.pop3.host=mail.tecnoweb.org.bo
mail.pop3.port=995
mail.username=grupo04sa@tecnoweb.org.bo
mail.password=tu_password

# Salida (SMTP)
mail.smtp.host=mail.tecnoweb.org.bo
mail.smtp.port=465

# Polling
mail.polling-interval=60000  # 60 segundos
```

## 📝 Formato de Comandos

Los comandos se envían en el **asunto del correo** con el siguiente formato:

```
COMANDO["parametro1","parametro2",numero3]
```

### Reglas:
- **Strings**: Entre comillas dobles (`"texto"`)
- **Números**: Sin comillas (`123`, `45.50`)
- **NULL**: Se omite el parámetro

### Ejemplos:

#### Usuarios
```
INSUSU["12345678","Juan","Pérez","Cliente","77123456","juan@email.com"]
LISUSU                          # Listar todos
LISUSU["Admin"]                 # Filtrar por rol
GETUSU["12345678"]              # Por CI
GETUSU[1]                       # Por ID
UPDUSU[1,"Juan Carlos",null,"77999888",null]  # Actualizar
DELUSU[1]                       # Eliminar
```

#### Boletos
```
INSBOL["A1",5,10,"Efectivo"]    # Vender boleto: asiento, viaje_id, cliente_id, metodo
LISBOL                          # Listar todos
LISBOL[5]                       # Por viaje
GETBOL[1]                       # Por ID
```

#### Encomiendas
```
INSENC[5,2,10,15.5,"María López",50,"origen","Efectivo"]
# viaje_id, ruta_id, cliente_id, peso, destinatario, precio, modalidad, metodo

LISENC                          # Listar todas
GETENC[1]                       # Por venta_id
```

## 🔐 Permisos por Rol

### Admin
- ✅ Todos los comandos de escritura y lectura
- ✅ CRUD completo de usuarios, vehículos, rutas, viajes
- ✅ Gestión de ventas y pagos

### Secretaria
- ✅ Venta de boletos (INSBOL)
- ✅ Registro de encomiendas (INSENC)
- ✅ Consultas de disponibilidad
- ❌ Eliminación de datos

### Conductor
- ✅ Consulta de viajes asignados
- ✅ Consulta de boletos de sus viajes
- ❌ Modificación de datos

### Cliente
- ✅ Consulta de sus propios boletos
- ✅ Consulta de sus encomiendas
- ❌ Acceso a datos de otros clientes

## 🚀 Flujo de Procesamiento

```
1. EmailScheduler ejecuta cada 60s
   ↓
2. EmailService lee correos no leídos (POP3)
   ↓
3. CommandParserService parsea asunto
   ↓
4. CommandValidatorService valida permisos del remitente
   ↓
5. CommandExecutorService ejecuta el comando
   ↓
6. ResponseFormatter genera respuesta en texto plano
   ↓
7. EmailService envía respuesta (SMTP)
   ↓
8. EmailLogService registra la operación
   ↓
9. EmailService marca correo como leído
```

## 📊 Auditoría

Cada comando ejecutado se registra en la tabla `email_logs`:

- Email del remitente
- Comando ejecutado
- Parámetros recibidos
- Respuesta generada
- Estado (EXITOSO/ERROR)
- Mensaje de error (si aplica)
- Tiempo de ejecución (ms)
- Timestamp

## 🧪 Ejemplo de Uso

### 1. Cliente envía correo
```
Para: sistema@transcomarapa.com
Asunto: INSBOL["A15",42,5,"Efectivo"]
Cuerpo: (vacío o texto libre)
```

### 2. Sistema responde
```
De: sistema@transcomarapa.com
Asunto: RE: INSBOL - EXITOSO
Cuerpo:
COMANDO EJECUTADO CORRECTAMENTE

- ID: 123
- Asiento: A15
- Fecha Venta: 14/12/2025 10:30
```

### 3. En caso de error
```
De: sistema@transcomarapa.com
Asunto: RE: INSBOL - ERROR
Cuerpo:
ERROR DE VALIDACIÓN

El asiento A15 ya está ocupado para este viaje.
```

## 🔨 Compilación y Ejecución

### Requisitos
- Java 17+
- Maven 3.6+
- PostgreSQL 12+

### Compilar
```bash
cd Sistema_ViaMail
mvn clean install
```

### Ejecutar
```bash
mvn spring-boot:run
```

### Generar JAR
```bash
mvn clean package
java -jar target/sistema-via-mail-0.0.1-SNAPSHOT.jar
```

## 📈 Estado de Implementación

### ✅ Completado (100%)

#### Infraestructura
- [x] Estructura Maven
- [x] Configuración application.properties
- [x] Clase principal con @EnableScheduling

#### Modelo de Datos
- [x] 9 entidades JPA con mapeo PostgreSQL
- [x] Relaciones @ManyToOne/@OneToMany
- [x] Métodos de utilidad (isAdmin(), isProgramado(), etc.)

#### Repositorios
- [x] 9 interfaces JpaRepository
- [x] Queries personalizados con @Query
- [x] Métodos derivados (findByRol, existsByCi)

#### DTOs y Utilidades
- [x] CommandRequest y CommandResponse
- [x] CommandValidator (regex, validaciones)
- [x] ResponseFormatter (texto plano)

#### Excepciones
- [x] CommandException
- [x] ValidationException
- [x] UnauthorizedException
- [x] EntityNotFoundException

#### Servicios Core
- [x] CommandParserService (parseo con regex)
- [x] EmailService (POP3/SMTP)
- [x] CommandValidatorService (permisos)
- [x] CommandExecutorService (orquestador)

#### Servicios de Negocio
- [x] UsuarioService (CRUD con validaciones)
- [x] BoletoService (venta con asientos)
- [x] EncomiendaService (modalidades pago)
- [x] EmailLogService (auditoría)

#### Automatización
- [x] EmailScheduler (@Scheduled cada 60s)
- [x] Manejo de errores robusto
- [x] Logging con SLF4J

#### Documentación
- [x] README.md completo
- [x] Comentarios en código
- [x] Este documento de implementación

## 🎓 Tecnologías Utilizadas

- **Spring Boot 3.2.1** - Framework base
- **Spring Data JPA** - Acceso a datos
- **PostgreSQL JDBC Driver** - Conexión BD
- **JavaMail API 2.0.1** - Procesamiento emails
- **BCrypt (Spring Security Crypto)** - Hash passwords
- **Lombok** - Reducción código boilerplate
- **SLF4J + Logback** - Logging

## 📞 Soporte

Para problemas o dudas:
- Email: grupo04sa@tecnoweb.org.bo
- Revisar logs en consola
- Verificar tabla `email_logs` en BD

## 📄 Licencia

Proyecto académico - Tecnología Web 2025
Trans Comarapa - Grupo04 SA

---

**Fecha de Implementación**: Diciembre 2025  
**Versión**: 1.0.0  
**Estado**: ✅ COMPLETO Y FUNCIONAL
