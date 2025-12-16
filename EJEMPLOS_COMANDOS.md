# COMANDOS DE EJEMPLO - Sistema Via Email

## 📧 Formato General
```
Asunto del correo: COMANDO["param1","param2",numero3]
```

---

## 👤 COMANDOS DE USUARIOS

### INSUSU - Insertar Usuario
```
INSUSU["12345678","Juan","Pérez","Cliente","77123456","juan@email.com"]
INSUSU["98765432","Ana","García","Secretaria","70987654","ana@email.com"]
INSUSU["11223344","Carlos","López","Conductor","76555444","carlos@email.com"]
```

Parámetros:
1. CI (string)
2. Nombre (string)
3. Apellido (string)
4. Rol (string): Admin, Secretaria, Conductor, Cliente
5. Teléfono (string)
6. Correo (string)

---

### LISUSU - Listar Usuarios
```
LISUSU                    # Todos los usuarios
LISUSU["Cliente"]         # Solo clientes
LISUSU["Admin"]           # Solo administradores
LISUSU["Secretaria"]      # Solo secretarias
LISUSU["Conductor"]       # Solo conductores
```

---

### GETUSU - Obtener Usuario
```
GETUSU["12345678"]        # Por CI
GETUSU[1]                 # Por ID
```

---

### UPDUSU - Actualizar Usuario
```
UPDUSU[1,"Juan Carlos","Pérez Gómez","77999888","nuevo@email.com"]
UPDUSU[1,"Juan Carlos",null,"77999888",null]    # Solo cambiar nombre y teléfono
```

Parámetros:
1. ID (número)
2. Nombre (string o null)
3. Apellido (string o null)
4. Teléfono (string o null)
5. Correo (string o null)

---

### DELUSU - Eliminar Usuario
```
DELUSU[1]                 # Eliminación lógica (soft delete)
```

---

## 🎫 COMANDOS DE BOLETOS

### INSBOL - Vender Boleto
```
INSBOL["A1",5,10,"Efectivo"]
INSBOL["B15",8,12,"Tarjeta"]
INSBOL["C20",5,10,"QR"]
```

Parámetros:
1. Asiento (string): A1, B2, C3...
2. Viaje ID (número)
3. Cliente ID (número)
4. Método pago (string): Efectivo, Tarjeta, QR

Validaciones:
- El asiento no debe estar ocupado
- El viaje debe estar en estado "Programado"
- Debe haber capacidad disponible
- El método de pago debe ser válido

---

### LISBOL - Listar Boletos
```
LISBOL                    # Todos los boletos
LISBOL[5]                 # Boletos del viaje 5
```

---

### GETBOL - Obtener Boleto
```
GETBOL[1]                 # Por ID de boleto
```

---

## 📦 COMANDOS DE ENCOMIENDAS

### INSENC - Registrar Encomienda
```
INSENC[5,2,10,15.5,"María López",50,"origen","Efectivo"]
INSENC[8,3,12,25,"Pedro Ramírez",80,"destino","Tarjeta"]
INSENC[5,2,10,10,"Ana Torres",45,"mixto","Efectivo"]
```

Parámetros:
1. Viaje ID (número)
2. Ruta ID (número)
3. Cliente ID (número)
4. Peso en kg (número decimal)
5. Nombre destinatario (string)
6. Precio (número decimal)
7. Modalidad pago (string): origen, destino, mixto
8. Método pago (string): Efectivo, Tarjeta, QR

Lógica de montos:
- **origen**: Todo se paga en origen
- **destino**: Todo se paga en destino
- **mixto**: 50% origen, 50% destino

Validaciones:
- Peso > 0
- Precio > 0
- Modalidad válida (origen, destino, mixto)
- Viaje debe existir y estar disponible

---

### LISENC - Listar Encomiendas
```
LISENC                    # Todas las encomiendas
```

---

### GETENC - Obtener Encomienda
```
GETENC[1]                 # Por ID de venta
```

---

## ⚠️ VALIDACIONES IMPORTANTES

### Permisos por Rol

#### Admin ✅
- Todos los comandos

#### Secretaria ✅
- INSBOL (vender boletos)
- INSENC (registrar encomiendas)
- LISBOL, LISENC (consultas)
- ❌ No puede eliminar usuarios ni modificar configuraciones

#### Conductor 🔍
- LISBOL (solo de sus viajes)
- Consultas de información
- ❌ No puede realizar ventas

#### Cliente 👤
- GETBOL (solo sus propios boletos)
- GETENC (solo sus encomiendas)
- ❌ Acceso muy restringido

---

## 🧪 ESCENARIOS DE PRUEBA

### 1. Flujo de Venta Completo
```
1. INSUSU["55555555","Cliente","Nuevo","Cliente","77111222","cliente@test.com"]
2. LISBOL[5]              # Ver disponibilidad del viaje 5
3. INSBOL["A10",5,15,"Efectivo"]  # Vender boleto al cliente 15
4. GETBOL[1]              # Verificar boleto creado
```

### 2. Registro de Encomienda
```
1. LISENC                 # Ver encomiendas existentes
2. INSENC[8,3,12,20,"Juan Díaz",60,"origen","Efectivo"]
3. GETENC[1]              # Verificar encomienda
```

### 3. Gestión de Usuarios
```
1. LISUSU["Cliente"]      # Listar clientes
2. GETUSU["12345678"]     # Buscar por CI
3. UPDUSU[5,"Nuevo Nombre",null,"77999000",null]
4. GETUSU[5]              # Verificar cambios
```

---

## 🚫 EJEMPLOS DE ERRORES

### Error de Permiso
```
Correo de: cliente@test.com
Asunto: DELUSU[1]

Respuesta:
RE: DELUSU - ERROR
No tienes permisos para ejecutar este comando.
```

### Error de Validación
```
Asunto: INSBOL["A1",5,10,"Efectivo"]  # Si A1 ya está ocupado

Respuesta:
RE: INSBOL - ERROR
El asiento A1 ya está ocupado en este viaje.
```

### Error de Formato
```
Asunto: INSBOL[A1,5,10,Efectivo]     # Falta comillas en strings

Respuesta:
RE: ERROR - COMANDO_INVALIDO
Error al parsear comando. Verifica el formato.
```

---

## 📋 CHECKLIST DE PRUEBAS

- [ ] Probar INSUSU con todos los roles
- [ ] Probar LISBOL con y sin filtro
- [ ] Vender boleto y verificar ocupación
- [ ] Intentar vender asiento ocupado (debe fallar)
- [ ] Registrar encomienda con cada modalidad
- [ ] Probar comando desde email sin permisos
- [ ] Verificar formato incorrecto
- [ ] Consultar boleto de otro cliente (debe fallar si es cliente)
- [ ] Listar usuarios siendo Admin vs Secretaria
- [ ] Actualizar usuario con parámetros NULL

---

## 🎯 COMANDOS RÁPIDOS

### Para Admin
```
LISUSU                    # Ver todos los usuarios
LISBOL                    # Ver todos los boletos
LISENC                    # Ver todas las encomiendas
```

### Para Secretaria
```
INSBOL["D5",10,8,"Efectivo"]          # Vender boleto
INSENC[10,2,8,12.5,"Test",35,"origen","Efectivo"]  # Registrar encomienda
```

### Para Cliente
```
GETBOL[5]                 # Ver mi boleto
GETENC[3]                 # Ver mi encomienda
```

---

**Nota**: Reemplaza los IDs de ejemplo con los IDs reales de tu base de datos.
