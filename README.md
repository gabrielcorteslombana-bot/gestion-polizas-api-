# API de Gestión de Pólizas de Arrendamiento

Prueba técnica para Desarrollador TI / Sénior Backend.  
Implementación en Spring Boot 3 con Java 17, arquitectura en capas, JPA con H2 en memoria y pruebas automatizadas con JUnit 5 / Mockito.

---

## 1. Arquitectura y Estructura del Proyecto

El proyecto está organizado en capas bajo principios de responsabilidad única y bajo acoplamiento:

```plaintext
com.seguros.polizas/
├── config/              # Inicialización de datos semilla, OpenAPI y RestTemplate
├── controller/          # Endpoints REST y mock de integración WebLogic
├── dto/                 # DTOs de entrada y salida con validaciones Jakarta
├── entity/              # Entidades JPA (Poliza, Riesgo) y enums de dominio
├── exception/           # Excepciones de negocio y GlobalExceptionHandler (@RestControllerAdvice)
├── repository/          # Interfaces Spring Data JPA
└── service/             # Lógica transaccional de negocio y cliente HTTP hacia CORE
```

---

## 2. Reglas de Negocio Implementadas

- **Pólizas Individuales:** Un tomador/asegurado (arrendatario) y un beneficiario (arrendador). Admite estrictamente un único riesgo.
- **Pólizas Colectivas:** Dirigidas a inmobiliarias y copropiedades. Permite agregar múltiples riesgos asociados.
- **Cálculo de Prima:** `valorPrima = canonMensual * mesesVigencia`.
- **Renovación con IPC:**
  - Incrementa el canon según el factor IPC indicado (por defecto 5% o 0.05).
  - Recalcula el valor de la prima manteniendo el período inicial de vigencia.
  - Pasa el estado a `RENOVADA`.
  - Si la póliza está en estado `CANCELADA`, se rechaza con error 400.
- **Cancelación en Cascada:** Al cancelar una póliza, todos sus riesgos asociados pasan a estado `CANCELADO`.
- **Adición de Riesgos:** El endpoint `POST /polizas/{id}/riesgos` valida que la póliza sea de tipo `COLECTIVA`. Si es `INDIVIDUAL`, retorna error 400.
- **Integración con CORE (WebLogic):** Toda mutación de estado invoca al endpoint `/core-mock/evento` con la cabecera `x-api-key: 123456`.

---

## 3. Endpoints Disponibles

### Pólizas
- `GET /polizas`: Lista pólizas con filtros opcionales `?tipo=COLECTIVA&estado=ACTIVA`.
- `GET /polizas/{id}`: Detalle de una póliza por ID.
- `POST /polizas`: Creación de póliza individual o colectiva.
- `GET /polizas/{id}/riesgos`: Lista los riesgos asociados a una póliza.
- `POST /polizas/{id}/renovar`: Renueva la póliza aplicando IPC.
- `POST /polizas/{id}/cancelar`: Cancela la póliza y sus riesgos en cascada.
- `POST /polizas/{id}/riesgos`: Agrega un nuevo riesgo (solo permitido en colectivas).

### Riesgos
- `GET /riesgos/{id}`: Detalle de un riesgo.
- `POST /riesgos/{id}/cancelar`: Cancela un riesgo puntual.

### Mock de Integración CORE
- `POST /core-mock/evento`: Simula el servicio agnóstico de edición en WebLogic.
  - Cabecera requerida: `x-api-key: 123456`.
  - Si la cabecera no se envía o es incorrecta, retorna HTTP 401 Unauthorized.

---

## 4. Ejemplos de Uso (cURL)

### Listar pólizas activas
```bash
curl -X GET "http://localhost:8080/polizas?estado=ACTIVA" -H "Accept: application/json"
```

### Renovar póliza con incremento del 5% de IPC
```bash
curl -X POST "http://localhost:8080/polizas/1/renovar" \
  -H "Content-Type: application/json" \
  -d '{"ipc": 0.05}'
```

### Agregar riesgo a póliza colectiva
```bash
curl -X POST "http://localhost:8080/polizas/1/riesgos" \
  -H "Content-Type: application/json" \
  -d '{
    "direccionInmueble": "Carrera 7 # 120-45 Oficina 502",
    "matriculaInmobiliaria": "50C-987654",
    "descripcion": "Consultorio médico",
    "valorAsegurado": 350000000.00
  }'
```

### Cancelar póliza (cascada)
```bash
curl -X POST "http://localhost:8080/polizas/1/cancelar"
```

### Invocar mock del CORE con seguridad
```bash
curl -X POST "http://localhost:8080/core-mock/evento" \
  -H "Content-Type: application/json" \
  -H "x-api-key: 123456" \
  -d '{
    "evento": "ACTUALIZACION",
    "polizaId": 1
  }'
```

---

## 5. Instrucciones de Ejecución

### Prerrequisitos
- JDK 17 o superior
- Maven 3.8+

### Compilación y Ejecución
```bash
# Compilar y correr pruebas
mvn clean test

# Iniciar la aplicación
mvn spring-boot:run
```

La aplicación iniciará en `http://localhost:8080` con datos semilla pre-cargados automáticamente (3 pólizas con riesgos de prueba).

- **Documentación Swagger UI:** `http://localhost:8080/swagger-ui.html`
- **Consola H2:** `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:mem:polizasdb`, usuario: `sa`, contraseña vacía).
