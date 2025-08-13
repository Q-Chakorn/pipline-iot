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

[![](https://mermaid.ink/img/pako:eNqtV9tyozgQ_RUVVZMng83V2A-7ldgGU5tNZpOpnZrBftCAYqgAYkFMLq78-wpa8jiJnSWp9Yvd0ml16_RpSd4qEY2JMlVuMnoXJbhi6Mt8VSD-OQ2vGbfXSFV_Q2fbK5plP3B0-_vTqgDAp0986vkHXV2en5-dzv54NQMuZ3wcfSN1t-asDbAhU1SJpdcAmsGsHtZ1wucLhu5SliBuqVFFYj6Q4qyWYB3QxvaaVD_TiCD2UJI2yW7WaAPGmOGMbjakUmG9zsMMr6-XiFFkjDRHM03NMEy5qAkQK4xiNExoToaM1CwmP2lZdz9TyiTWAqwdxjS65SEimpe0Jki9QZb2MrT2gPMMxfSukO42uDthTWKkpqgpuQ9Bac7xiPPTZvjl1JdwB-Djd0RrSqTG631CUvzPPhPuG0y4AJn0YWIiCjc6kJyp7YK-5kAfCU-9Jwu6qLpu9An1jIBx5_k9XBS7Id2AsbeEPV98Pr_8hi4WX9Hfi6vr4PLiLYlf0G7FeRglJLqlDUN1lKMTFGUEF19b7QJ43sEWshE2KYsyWhCR2AJm9ZCPo24CJYyV9XQ45CNJ80Pjux7-pc4SfEurQpZD45NyBeBpYbTl-6WOz5j308kJyngjZlhigYWF2WI5g69AAga94cmcfzRpFkOhaoSLGJVNnbTlOp1diZU98NDDro_xY1ORA53sQa7e4U72jneyZ_bYnSfStkL8iDguLbhdQyC1rNIiSkucSTD0tGe3YBxVO4cC5zwtoJmPSzj0sOcIMQIn94IaVS0zzG5olaMsLZr7Ic5jxxqI31Xu8GBsb1GtI4h_p3T4cr9T3gPtgi3F2nqfmect7Y2PFbHzgB7w3F5cwAngTXpyMRHFHv3vZOy2-JIFERmORl8Ks2yyTBwgNxXN9_UozhsfTB-MfveNDyr1jbfF7EMz-eZBMfvmUTH71vHD2AdZ-nafuvkgSt_pVzdfsCcvFqCvvyrX-zt7Lkb_jfvFB3X5k15bmohKjXruSdaZ3yv_vatn8tqpyodeWe5UVZESV2QXAeSwBHMJRj8lLYWrcZydJahoaW7nKU-T0epBJo_IfVqzWipqae6_rJbdw0Uiefu3F0i79fU-WlxSS_tj94tYCjQZSH5iUmb0QcYBDQZgBWD0YycAdoLDt0Fw_DYIzB5PlQBug8D62KMtEJu2P_gKCw4c2YHTJ2_o0mD8zhdWACoO3Pc8l4SrLR9HneVKSxkomyqNlSmrGjJQclLluDWVbYtcKSwhOVkpU_4zJje4ydhKWRVP3K3ExXdKc-lZ0WaTSAPeffMUbyr8C0GKmFQz2hRMmVruqFtCmW6Ve2Wqmq42Me2xYdrWyHUNd6A88FF9ZOiabbsj3ZwYtmOP3KeB8thFtTTHGtv62HF13dBdwxkoJE55b_0J_4S6P0RP_wKF3_3j?type=png)](https://mermaid.live/edit#pako:eNqtV9tyozgQ_RUVVZMng83V2A-7ldgGU5tNZpOpnZrBftCAYqgAYkFMLq78-wpa8jiJnSWp9Yvd0ml16_RpSd4qEY2JMlVuMnoXJbhi6Mt8VSD-OQ2vGbfXSFV_Q2fbK5plP3B0-_vTqgDAp0986vkHXV2en5-dzv54NQMuZ3wcfSN1t-asDbAhU1SJpdcAmsGsHtZ1wucLhu5SliBuqVFFYj6Q4qyWYB3QxvaaVD_TiCD2UJI2yW7WaAPGmOGMbjakUmG9zsMMr6-XiFFkjDRHM03NMEy5qAkQK4xiNExoToaM1CwmP2lZdz9TyiTWAqwdxjS65SEimpe0Jki9QZb2MrT2gPMMxfSukO42uDthTWKkpqgpuQ9Bac7xiPPTZvjl1JdwB-Djd0RrSqTG631CUvzPPhPuG0y4AJn0YWIiCjc6kJyp7YK-5kAfCU-9Jwu6qLpu9An1jIBx5_k9XBS7Id2AsbeEPV98Pr_8hi4WX9Hfi6vr4PLiLYlf0G7FeRglJLqlDUN1lKMTFGUEF19b7QJ43sEWshE2KYsyWhCR2AJm9ZCPo24CJYyV9XQ45CNJ80Pjux7-pc4SfEurQpZD45NyBeBpYbTl-6WOz5j308kJyngjZlhigYWF2WI5g69AAga94cmcfzRpFkOhaoSLGJVNnbTlOp1diZU98NDDro_xY1ORA53sQa7e4U72jneyZ_bYnSfStkL8iDguLbhdQyC1rNIiSkucSTD0tGe3YBxVO4cC5zwtoJmPSzj0sOcIMQIn94IaVS0zzG5olaMsLZr7Ic5jxxqI31Xu8GBsb1GtI4h_p3T4cr9T3gPtgi3F2nqfmect7Y2PFbHzgB7w3F5cwAngTXpyMRHFHv3vZOy2-JIFERmORl8Ks2yyTBwgNxXN9_UozhsfTB-MfveNDyr1jbfF7EMz-eZBMfvmUTH71vHD2AdZ-nafuvkgSt_pVzdfsCcvFqCvvyrX-zt7Lkb_jfvFB3X5k15bmohKjXruSdaZ3yv_vatn8tqpyodeWe5UVZESV2QXAeSwBHMJRj8lLYWrcZydJahoaW7nKU-T0epBJo_IfVqzWipqae6_rJbdw0Uiefu3F0i79fU-WlxSS_tj94tYCjQZSH5iUmb0QcYBDQZgBWD0YycAdoLDt0Fw_DYIzB5PlQBug8D62KMtEJu2P_gKCw4c2YHTJ2_o0mD8zhdWACoO3Pc8l4SrLR9HneVKSxkomyqNlSmrGjJQclLluDWVbYtcKSwhOVkpU_4zJje4ydhKWRVP3K3ExXdKc-lZ0WaTSAPeffMUbyr8C0GKmFQz2hRMmVruqFtCmW6Ve2Wqmq42Me2xYdrWyHUNd6A88FF9ZOiabbsj3ZwYtmOP3KeB8thFtTTHGtv62HF13dBdwxkoJE55b_0J_4S6P0RP_wKF3_3j)
