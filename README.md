# 🚌 Sistema Via Mail - Trans Comarapa

Sistema de gestión de transporte interprovincial **vía correo electrónico** desarrollado con Spring Boot.

## 📋 Descripción

Sistema que permite gestionar ventas de boletos, encomiendas, viajes, vehículos y usuarios mediante comandos enviados por correo electrónico. Los usuarios envían comandos en el asunto del correo y reciben respuestas automáticas en menos de 60 segundos.

**Grupo04 SA - Tecnología Web INF513**

---

## 🛠️ Tecnologías

- **Spring Boot 3.2.1**
- **Spring Data JPA**
- **Spring Mail**
- **PostgreSQL**
- **JavaMail API**
- **Lombok**
- **Maven**

---

## 📦 Requisitos Previos

- Java 17 o superior
- Maven 3.8+
- PostgreSQL 12+
- Servidor de correo POP3/SMTP configurado

---

## ⚙️ Configuración

### 1. Base de Datos

El sistema se conecta a la **misma base de datos** del proyecto Laravel TransComarapa.

Asegúrate de tener la base de datos creada y las migraciones ejecutadas:

```bash
cd TransComarapa
php artisan migrate
```

### 2. Correo Electrónico

Configurar las credenciales en `application.properties`:

```properties
mail.pop3.username=grupo04sa@tecnoweb.org.bo
mail.pop3.password=tu_password
spring.mail.username=grupo04sa@tecnoweb.org.bo
spring.mail.password=tu_password
```

### 3. Compilar y Ejecutar

```bash
# Compilar el proyecto
mvn clean install

# Ejecutar la aplicación
mvn spring-boot:run
```

O usar el JAR generado:

```bash
java -jar target/sistema-via-mail-1.0.0.jar
```

---

## 📧 Formato de Comandos

Los comandos se envían en el **asunto del correo** con el siguiente formato:

```
COMANDO["param1","param2",param3]
```

- **Strings** van entre comillas dobles: `"texto"`
- **Números** van sin comillas: `123`, `45.50`
- **Valores NULL** se omiten

### Ejemplos:

```
INSUSU["1234567","Juan","Pérez","Cliente","71234567","juan@mail.com"]
LISBOL[1]
INSBOL["A12",1,5,"Efectivo"]
```

---

## 🎯 Comandos Disponibles

### Usuarios

| Comando | Descripción | Parámetros |
|---------|-------------|------------|
| `INSUSU` | Insertar usuario | CI, Nombre, Apellido, Rol, Teléfono, Email |
| `LISUSU` | Listar usuarios | Rol (opcional) |
| `GETUSU` | Obtener usuario | ID o CI |
| `UPDUSU` | Actualizar usuario | ID, campos a actualizar |
| `DELUSU` | Eliminar usuario | ID |

### Vehículos

| Comando | Descripción | Parámetros |
|---------|-------------|------------|
| `INSVEH` | Insertar vehículo | Placa, Marca, Modelo, Año, Color, ConductorID |
| `LISVEH` | Listar vehículos | - |
| `GETVEH` | Obtener vehículo | ID o Placa |
| `UPDVEH` | Actualizar vehículo | ID, campos |
| `DELVEH` | Eliminar vehículo | ID |

### Rutas

| Comando | Descripción | Parámetros |
|---------|-------------|------------|
| `INSRUT` | Insertar ruta | Origen, Destino, Nombre |
| `LISRUT` | Listar rutas | - |
| `GETRUT` | Obtener ruta | ID |

### Viajes

| Comando | Descripción | Parámetros |
|---------|-------------|------------|
| `INSVIA` | Insertar viaje | RutaID, VehiculoID, FechaSalida, Precio, AsientosTotales |
| `LISVIA` | Listar viajes | - |
| `GETVIA` | Obtener viaje | ID |

### Boletos

| Comando | Descripción | Parámetros |
|---------|-------------|------------|
| `INSBOL` | Vender boleto | Asiento, ViajeID, ClienteID, MetodoPago |
| `LISBOL` | Listar boletos | ViajeID (opcional) |
| `GETBOL` | Obtener boleto | ID |

### Encomiendas

| Comando | Descripción | Parámetros |
|---------|-------------|------------|
| `INSENC` | Registrar encomienda | ViajeID, RutaID, ClienteID, Peso, Destinatario, Precio, ModalidadPago, MetodoPago |
| `LISENC` | Listar encomiendas | - |
| `GETENC` | Obtener encomienda | VentaID |

### Ventas y Pagos

| Comando | Descripción | Parámetros |
|---------|-------------|------------|
| `LISVEN` | Listar ventas | FechaDesde, FechaHasta |
| `GETVEN` | Obtener venta | ID |
| `LISPAG` | Listar pagos | VentaID |

---

## 🔐 Seguridad y Permisos

El sistema valida que el remitente del correo esté registrado en la tabla `usuarios`.

### Roles y Permisos:

- **Admin**: Acceso total a todos los comandos
- **Secretaria**: Gestión de ventas (boletos y encomiendas), consultas
- **Conductor**: Solo consultas de sus viajes asignados
- **Cliente**: Solo consultas de sus propias compras

---

## 📊 Auditoría

Todos los comandos ejecutados se registran en la tabla `email_logs`:

- Email remitente
- Comando ejecutado
- Parámetros
- Respuesta enviada
- Estado (EXITOSO/ERROR)
- Tiempo de ejecución

---

## 🔄 Funcionamiento del Scheduler

El sistema revisa la bandeja de entrada **cada 60 segundos**:

1. **Lee** correos no leídos via POP3
2. **Parsea** el asunto para extraer comando y parámetros
3. **Valida** sintaxis y permisos del usuario
4. **Ejecuta** el comando
5. **Formatea** la respuesta
6. **Envía** email de respuesta via SMTP
7. **Registra** en auditoría (email_logs)

---

## 📁 Estructura del Proyecto

```
src/main/java/com/grupo04sa/sistema_via_mail/
├── model/              # Entidades JPA (Usuario, Venta, Boleto, etc.)
├── repository/         # Interfaces JPA Repository
├── service/            # Servicios de lógica de negocio
│   ├── email/         # Procesamiento de correos
│   └── business/      # Lógica de dominio
├── scheduler/          # Tareas programadas
├── dto/                # Data Transfer Objects
├── util/               # Utilidades (Validator, Formatter)
├── exception/          # Excepciones personalizadas
└── SistemaViaMailApplication.java
```

---

## 🧪 Testing

```bash
# Ejecutar tests
mvn test

# Ejecutar con cobertura
mvn test jacoco:report
```

---

## 📝 Logs

Los logs se guardan en `logs/sistema-via-mail.log` con rotación diaria.

Niveles de log:
- INFO: Operaciones normales
- DEBUG: Detalles de procesamiento
- ERROR: Errores y excepciones

---

## 🤝 Contribuciones

**Grupo04 SA**
- Proyecto académico - Tecnología Web INF513
- Universidad: UAGRM - Santa Cruz, Bolivia
- Fecha: Diciembre 2025

---

## 📄 Licencia

Este proyecto es de uso académico para la materia Tecnología Web.

---

## 📞 Contacto

**Email:** grupo04sa@tecnoweb.org.bo

---

## ⚠️ Notas Importantes

1. Asegúrate de ejecutar las migraciones de Laravel primero
2. El sistema NO modifica el esquema de la base de datos (ddl-auto=validate)
3. Los correos se procesan cada 60 segundos (configurable en application.properties)
4. Mantén sincronizadas las credenciales de correo en ambos proyectos

---

**🚀 ¡Sistema listo para gestionar Trans Comarapa desde tu correo!**
