
## 4. ARQUITECTURA DEL SISTEMA SPRING BOOT

### 4.1. Estructura de Paquetes

```
com.example.sistema_via_mail/
├── config/              # Configuraciones (Mail, JPA, etc.)
├── model/               # Entidades JPA
│   ├── Usuario.java
│   ├── Vehiculo.java
│   ├── Ruta.java
│   ├── Boleto.java
│   ├── Encomienda.java
│   ├── Venta.java
│   ├── Pago.java
│   └── Reporte.java
├── repository/          # Interfaces JPA Repository
│   ├── UsuarioRepository.java
│   ├── VehiculoRepository.java
│   ├── RutaRepository.java
│   ├── BoletoRepository.java
│   ├── EncomiendaRepository.java
│   ├── VentaRepository.java
│   ├── PagoRepository.java
│   └── ReporteRepository.java
├── service/             # Lógica de negocio
│   ├── EmailService.java
│   ├── CommandParserService.java
│   ├── CommandExecutorService.java
│   ├── UsuarioService.java
│   ├── VehiculoService.java
│   ├── RutaService.java
│   ├── BoletoService.java
│   ├── EncomiendaService.java
│   ├── VentaService.java
│   ├── PagoService.java
│   └── ReporteService.java
├── scheduler/           # Tareas programadas
│   └── EmailScheduler.java
├── dto/                 # Data Transfer Objects
│   ├── CommandRequest.java
│   └── CommandResponse.java
├── exception/           # Manejo de excepciones
│   ├── CommandException.java
│   └── ValidationException.java
└── util/                # Utilidades
    ├── CommandValidator.java
    └── ResponseFormatter.java
```