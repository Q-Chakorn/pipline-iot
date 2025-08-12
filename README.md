# IoT Pipeline Jenkins

## Pipeline สำหรับ Deploy IoT Services

### Overview
Jenkins pipeline สำหรับ build และ deploy IoT services ไปยัง Azure Container Registry และ production server

### Services ที่รองรับ
- **datalogger-agent** - Service สำหรับเก็บข้อมูล sensor
- **iaq-agent** - Indoor Air Quality monitoring service

### Parameters
- `SERVICES`: เลือก service ที่ต้องการ deploy
- `TAG`: Docker image tag version
- `ROLLBACK`: เปิดใช้เพื่อ rollback ไปยัง version ก่อนหน้า

### Pipeline Flow

#### Normal Deployment
1. **Git Clone** - Clone source code จาก GitHub
2. **Build & Push** - Build Docker images (multi-arch) และ push ไปยัง Azure Container Registry
3. **Pull Image** - Pull image ล่าสุดไปยัง production server
4. **Prepare** - Update source code บน production server
5. **Deploy** - Deploy service ด้วย docker-compose

#### Rollback Mode
- Deploy service ด้วย Docker image tag ที่ระบุ
- ไม่ผ่านขั้นตอน build ใหม่

### Infrastructure
- **Build Agent**: macbook node
- **Registry**: testiotacr.azurecr.io (Azure Container Registry)
- **Production**: 20.6.33.223 (SSH deployment)
- **Docker Compose Files**: 
  - `4.datalogger-agent.yaml`
  - `3.iaq-agent.yaml`

### Requirements
- Jenkins credentials: `ssh-credentials`, `azure-credentials`
- Docker buildx สำหรับ multi-platform builds
- Azure CLI access
