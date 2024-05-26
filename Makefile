# Variáveis
GRADLE = ./gradlew
MAIN_CLASS = org.App.Menu
ARGS = 192.168.1.100 50050
WORK_DIR = /Users/monicaaraujo/Desktop/2S/ssd/PublicLedgerAuctionSDS

# Regra padrão
all: run

# Regra para compilar os arquivos fonte usando Gradle
build:
	@echo "Compilando os arquivos fonte com Gradle..."
	cd $(WORK_DIR) && $(GRADLE) build
	@echo "Compilação concluída."

# Regra para executar a aplicação usando Gradle
run: build
	@echo "Executando a aplicação com Gradle..."
	cd $(WORK_DIR) && $(GRADLE) run -PmainClass=$(MAIN_CLASS) --args="$(ARGS)"
	@echo "Execução concluída."

# Regra para limpar os arquivos compilados usando Gradle
clean:
	@echo "Limpando arquivos compilados com Gradle..."
	cd $(WORK_DIR) && $(GRADLE) clean
	@echo "Limpeza concluída."

.PHONY: all build run clean
