# DockerCompose-Southern Ocean Sentinel
Este projeto é composto por um backend em Java e uma API de modelo do Roboflow escrita em Python. Usamos Docker e Docker Compose para orquestrar os contêineres.<br> 
Este README fornece instruções detalhadas para clonar, construir e executar o projeto.

# Estrutura do Projeto
***
DockerCompose/<br>
├── SouthernOceanSentinel_API/<br>
│   ├── build.gradle<br>
│   ├── Dockerfile<br>
│   ├── settings.gradle<br>
│   └── src/<br>
├── RoboflowModelAPI/<br>
│   ├── Dockerfile<br>
│   ├── main.py<br>
│   ├── requirements.txt<br>
├── docker-compose.yml<br>
└── README.md<br>
***
# Pré-requisitos

Git<br>
Docker<br>
Docker Compose<br>

# Passo a Passo para Configuração

1. Clonar o Repositório<br>
  Primeiro, clone o repositório em sua máquina local:<br>
***
git clone https://github.com/NEXTGEN-FIAP/DockerCompose-SOS.git<br>
cd DockerCompose<br>
***
2. Dockerfile do Backend (Java)<br>
  Certifique-se de que o Dockerfile do backend está configurado corretamente na pasta SouthernOceanSentinel_API/:<br>
***
#Definir um argumento para a versão do Gradle e do JDK<br>
ARG GRADLE_VERSION=8.0<br>
ARG JAVA_VERSION=17<br>
ARG APP_VERSION=1.0.0<br>

#Usar a imagem oficial do Gradle com JDK 17 para criar um artefato de build.<br>
#Esta será a fase de build.<br>
FROM gradle:${GRADLE_VERSION}-jdk${JAVA_VERSION} AS build<br>
WORKDIR /home/gradle/project<br>

#Copiar os arquivos de configuração do Gradle e o código fonte para o contêiner.<br>
COPY build.gradle settings.gradle /home/gradle/project/<br>
COPY src /home/gradle/project/src<br>

#Definir uma variável de ambiente para a versão do aplicativo<br>
ENV APP_VERSION=${APP_VERSION}<br>

#Executar o build do Gradle<br>
RUN gradle build --no-daemon<br>

#Usar a imagem oficial do Eclipse Temurin para rodar a aplicação<br>
FROM eclipse-temurin:${JAVA_VERSION}-jre<br>
WORKDIR /app<br>

#Criar um usuário e grupo não root<br>
RUN groupadd -g 1001 appgroup && useradd -u 1001 -g appgroup -m appuser<br>

#Instalar Python e PIP<br>
RUN apt-get update && \<br>
    apt-get install -y python3 python3-pip && \
    ln -sf /usr/bin/python3 /usr/bin/python && \
    ln -sf /usr/bin/pip3 /usr/bin/pip && \
    pip install --upgrade pip setuptools && \
    rm -rf /var/lib/apt/lists/*

#Copiar o artefato de build do contêiner de build para o contêiner final<br>
COPY --from=build /home/gradle/project/build/libs/*.jar /app/SouthernOceanSentinelApiApplication-${APP_VERSION}.jar<br>

#Mudar a propriedade dos arquivos para o usuário não root<br>
RUN chown -R appuser:appgroup /app<br>

#Alternar para o usuário não root<br>
USER appuser<br>

#Expor a porta 8080<br>
EXPOSE 8080<br>

#Comando para rodar a aplicação<br>
CMD ["java", "-jar", "/app/SouthernOceanSentinelApiApplication-${APP_VERSION}.jar"]<br>

***
3. Dockerfile do RoboflowModelAPI (Python)<br>
Certifique-se de que o Dockerfile do RoboflowModelAPI está configurado corretamente na pasta RoboflowModelAPI/:<br>

#Usar a imagem oficial do Python<br>
FROM python:3.8-slim<br>

#Definir o diretório de trabalho<br>
WORKDIR /app<br>

#Copiar os arquivos de requisitos para o contêiner<br>
COPY requirements.txt /app/<br>

#Instalar as dependências do Python<br>
RUN pip install --no-cache-dir -r requirements.txt<br>

#Copiar o código da aplicação para o contêiner<br>
COPY main.py /app/<br>

#Expor a porta em que o serviço vai rodar<br>
EXPOSE 8000<br>

#Comando para rodar a aplicação<br>
CMD ["uvicorn", "main:app", "--host", "0.0.0.0", "--port", "8000"]<br>

***
4. Docker Compose<br>
O arquivo docker-compose.yml deve estar configurado conforme abaixo para orquestrar os serviços:<br>

version: "3"<br>

services:<br>
  #Serviço do banco de dados Oracle<br>
  db:<br>
    image: oracleinanutshell/oracle-xe-11g<br>
    restart: always<br>
    environment:<br>
      - ORACLE_ALLOW_REMOTE=true<br>
      - ORACLE_DISABLE_ASYNCH_IO=true<br>
      - ORACLE_PWD=rootpassword<br>
    ports:<br>
      - "1521:1521"  #Mapeamento da porta do host para a porta do contêiner do Oracle<br>
    volumes:<br>
      - db_data:/u01/app/oracle  #Volume para persistência dos dados do Oracle<br>
    networks:<br>
      - SOS_network  #Conexão à rede personalizada<br>

  #Serviço do back-end (Spring Boot)<br>
  backend:<br>
    build: ./SouthernOceanSentinel_API<br>  #Build do Dockerfile no diretório backend<br>
    ports:<br>
      - "8080:8080"  #Mapeamento da porta do host para a porta do contêiner do Spring Boot<br>
    depends_on:<br>
      - db  #Dependência do serviço db<br>
    environment:<br>
      SPRING_DATASOURCE_URL: jdbc:oracle:thin:@db:1521:xe  # URL do banco de dados Oracle<br>
      SPRING_DATASOURCE_USERNAME: system  # Usuário do banco de dados Oracle<br>
      SPRING_DATASOURCE_PASSWORD: rootpassword  # Senha do banco de dados Oracle<br>
      SPRING_JPA_HIBERNATE_DDL_AUTO: update  # Configuração do Hibernate para atualizar o esquema<br>
      SPRING_JPA_DATABASE_PLATFORM: org.hibernate.dialect.Oracle10gDialect  # Plataforma do banco de dados Oracle<br>
    networks:<br>
      - SOS_network  #Conexão à rede personalizada<br>

  #Serviço da API externa (Python)<br>
  external-api:<br>
    build: ./RoboflowModelAPI  #Build do Dockerfile no diretório external-api<br>
    ports:<br>
      - "8000:8000"  #Mapeamento da porta do host para a porta do contêiner da API externa<br>
    networks:<br>
      - SOS_network  #Conexão à rede personalizada<br>

networks:<br>
  SOS_network:  #Definição de uma rede personalizada para comunicação entre os serviços<br>

volumes:<br>
  db_data:  #Definição de um volume para persistência dos dados do Oracle<br>

  ***
5. Construir e Executar os Contêineres<br>
Execute o comando a seguir para construir e iniciar os contêineres:<br>

docker-compose up --build<br>

***
6. Verificar o Funcionamento dos Serviços<br>
Após a inicialização dos contêineres, verifique se os serviços estão funcionando corretamente.<br>

Verificar o Backend (Java)<br>
Abra um navegador e acesse http://localhost:8080. A aplicação Java deve estar rodando e você deve ver a página inicial ou endpoint configurado.<br>

Verificar o RoboflowModelAPI (Python)<br>
Abra um navegador e acesse http://localhost:8000. A aplicação Python deve estar rodando e você deve ver a página inicial ou endpoint configurado.<br>

***
7. Testar Persistência de Dados<br>
Para testar a persistência de dados, conecte-se ao banco de dados Oracle e verifique se os dados estão sendo armazenados corretamente.<br>

Abra o terminal e conecte-se ao contêiner do Oracle:<br>

docker exec -it southern-ocean-sentinel_db_1 bash<br>

***
No terminal do contêiner, conecte-se ao banco de dados Oracle:<br>

sqlplus system/rootpassword@//localhost:1521/xe<br>
                                                 
***
Execute consultas SQL para verificar os dados, por exemplo<br>

SELECT * FROM sua_tabela;<br>

***
Seguindo estes passos, você será capaz de configurar, construir e executar os contêineres Docker para os serviços backend e RoboflowModelAPI, e verificar a persistência de dados no banco de dados Oracle.

