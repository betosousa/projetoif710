# Memória

A análise dos testes foi realizada com um emulador do Nexus 4 utilizando a API 25. Dividimos a avaliação de Memória em ações que representam os pricipais usos do app.

## App em primeiro plano

### Análise

Pelo que se pode perceber, o aplicativo usou em média 12.5 MB de memória, quando em primeiro plano com a MainActivity. Vide imagem abaixo.

![Alt memory_main_activity](Imgs/memory_main_activity.png)
[//]: <> (inserir possível imagem)

### Justificativa

Este comportamento pode ser justificado pelas operações relativas ao banco de dados que são efetuadas quando o app é inicializado (vide trecho de código abaixo) para atualizar a lista dos episódios de podcast junto ao espaço de memória restante necessário para variáveis, código, etc. necessários ao app quando em primeiro plano. Além disto, há as informações dos views que são necessários para disponibilizar a lógica ao usuário.

```java

	@Override
    protected void onStart() {
        super.onStart();
        // atualiza lista com itens ja salvos no BD
        new ProviderTask().execute();
    }

```


## App em primeiro plano

### Análise

Quando em background, o aplicativo usou em média 9.9 MB de memória. Vide imagem abaixo.

![Alt memory_main_background](Imgs/memory_main_background.png)

### Justificativa

Uma possível explicação seria a ausência das informações visuais do app para serem disponibilizadas ao app além do fato de não precisar de memória para o app quando este está em background, visto que o usuário não irá interagir com ele neste momento.

## Visualização dos detalhes do episódio

### Análise

Ao acessar a activity que disponibiliza as informações do episódio do podcast (EpisodeDetailActivity), não há modificações notáveis no uso da memória. Porém ao retornar a MainActivity, há um leve incremento no uso da memória.

### Justificativa

Isto é possivelmente explicado pelo carregamento desta junto a remoção da EpisodeDetailActivity previamente carregada.

## Reprodução de áudio do podcast

### Análise

Este mesmo comportamento é visto também no PlayActivity, que é uma activity chamada quando o usuário deseja reproduzir o episódio baixado. O service presente nesta activity responsável pela reprodução do áudio não apresenta notáveis variações no uso da memória.

### Justificativa

Isto pode ser justificado da mesma forma que o item anterior.