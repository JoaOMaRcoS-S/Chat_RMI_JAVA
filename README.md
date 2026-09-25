# ChatSD — Chat com Java RMI

Chat em sala única desenvolvido em Java com **RMI (Remote Method Invocation)**, como estudo de Sistemas Distribuídos. Um servidor central mantém a lista de alunos conectados e repassa cada mensagem a todos eles usando **callbacks remotos**.

## Funcionalidades

- Entrada na sala com nome de usuário (nomes com espaço são aceitos)
- Bloqueio de nomes repetidos: o cliente é avisado e pode escolher outro nome
- Quem entra recebe a lista de quem já está na sala (ou o aviso de que é o primeiro)
- Aviso para todos quando alguém entra ou sai
- Mensagens entregues a todos com o nome do remetente; as próprias aparecem marcadas com `(você)`
- Prompt com o nome de quem está digitando (`maria> `)
- Remoção automática de clientes que caíram sem usar `/sair`

## Arquitetura

```
src/
├── module-info.java
├── chatServerInterface/
│   └── ChatServerInterface.java       # contrato remoto do servidor
├── chatServerImplementacao/
│   └── ChatServerImplementacao.java   # lógica da sala (registro, broadcast)
├── chatClienteInterface/
│   └── ChatClienteInterface.java      # contrato remoto do cliente (callback)
├── chatClienteImplementacao/
│   └── ChatClienteImplementacao.java  # recebe e exibe as mensagens
└── main/
    ├── Main.java                      # inicia o servidor
    └── ClienteMain.java               # inicia um cliente
```

### Como a comunicação funciona

A comunicação acontece nos dois sentidos, e os dois lados são objetos remotos:

1. O `Main` cria o registry RMI na porta **1050** e publica o servidor com o nome **`ChatSD`**.
2. O `ClienteMain` faz `lookup("ChatSD")` e obtém um *stub* do servidor.
3. O cliente cria um `ChatClienteImplementacao` (que estende `UnicastRemoteObject`) e o envia no `registerClient`. O servidor recebe um stub que aponta de volta para o cliente.
4. Quando alguém chama `sendMessage`, o servidor percorre os clientes registrados e chama `receiveMessage` em cada um. Esse é o **callback**.
5. Se o callback de um cliente falha, o servidor o remove da sala.

```
 ClienteMain ──lookup/registerClient/sendMessage──▶ ChatServerImplementacao
      ▲                                                    │
      └──────────────── receiveMessage (callback) ─────────┘
```

## Requisitos

- Java **21** (JDK)
- Eclipse IDE (opcional, para editar e compilar)

## Como executar

### Pelo terminal (recomendado para vários clientes)

Com o projeto compilado (o Eclipse gera as classes na pasta `bin`), abra um terminal na pasta do projeto.

**1. Servidor** — em uma janela:

```powershell
java -p bin -m chatCliente/main.Main
```

Aguarde a mensagem `Servidor ChatSD rodando na porta 1050`.

**2. Clientes** — uma nova janela para cada aluno:

```powershell
java -p bin -m chatCliente/main.ClienteMain
```

Para conectar a um servidor em outra máquina, passe o IP como argumento:

```powershell
java -p bin -m chatCliente/main.ClienteMain 192.168.0.10
```

Para compilar sem o Eclipse:

```powershell
javac -d bin (Get-ChildItem -Recurse src -Filter *.java).FullName
```

### Pelo Eclipse

1. Execute `Main` (Run As → Java Application).
2. Execute `ClienteMain` uma vez para cada aluno.
3. Use o ícone **Display Selected Console** da aba Console para alternar entre os consoles.

> Com três ou mais clientes, o Eclipse troca o console da frente sozinho e é fácil digitar no aluno errado. Nesse caso, prefira o terminal.

### Comandos no chat

| Comando | Ação |
|---|---|
| texto + Enter | envia a mensagem para a sala |
| `/sair` | sai da sala e encerra o cliente |

## Exemplo de uso

```
Digite seu nome:
maria de lima
[ChatSD] Já estão na sala: joao marcos de moura
[ChatSD] maria de lima entrou na sala
Conectado! Digite suas mensagens (/sair para sair).
maria de lima> bom dia joao, tudo bem com voce
[maria de lima (você)] bom dia joao, tudo bem com voce
maria de lima>
[joao marcos de moura] tudo certo!
maria de lima>
```

## Problemas comuns

| Erro | Causa | Solução |
|---|---|---|
| `Connection refused to host: localhost` | O servidor não está rodando ou caiu ao iniciar | Rode o `Main` antes dos clientes e confira o console dele |
| `module chatCliente does not "exports ..." to module java.rmi` | Pacotes das interfaces não exportados | Adicione os `exports` no `module-info.java` (veja abaixo) |
| `argument type mismatch` | `ChatClienteInterface` não estende `Remote` | Declare `extends Remote` na interface |
| `Port already in use: 1050` | Um servidor antigo ainda está rodando | Encerre o processo anterior (comando abaixo) |
| Acentos digitados viram `?` | Codificação do terminal diferente da do `Scanner` | Crie o `Scanner` com `System.console().charset()` |

`module-info.java` necessário:

```java
module chatCliente {
	requires java.rmi;
	exports chatServerInterface;
	exports chatClienteInterface;
}
```

Encerrar um servidor preso na porta (PowerShell):

```powershell
Stop-Process -Id (Get-NetTCPConnection -LocalPort 1050 -State Listen).OwningProcess -Force
```

## Possíveis melhorias

- Mensagens privadas (`/msg nome texto`)
- Comando para listar quem está online
- Interface gráfica com Swing ou JavaFX
- Histórico das últimas mensagens para quem acabou de entrar

## Autor

Seu Nome — [github.com/seu-usuario](https://github.com/seu-usuario)
