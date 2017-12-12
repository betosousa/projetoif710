# Testes

Os testes foram especificados antes da refatorção para uso de Room e Architecture components.

## Unitários (JUnit)

Nesta etapa, iremos definir uma suite de testes para as operações de CRUD definidas na classe PodcastProviderHelper, que trata objetos do tipo ItemFeed, principal classe do modelo e que representa um podcast salvo no app.

Para isso, implementamos a classe JUnitTestSuite no pacote androidTest porque precisamos do contexto da aplicação para testar o BD.

Esta classe possui os seguintes atributos:

```java
    
    private static final long DOWNLOAD_ID = 10;
    private static final int ITEM_ID = 1;
    static Context context;
    static ItemFeed itemFeed;
    int itensTotal;
```

Os métodos da classe seram explicados a seguir.

### beforeClass()

Neste método colocamos tudo que fosse necessário e comum a todos os testes.

```java

    @BeforeClass
    public static void beforeClass() {
        // recupera o contexto do app
        context = InstrumentationRegistry.getTargetContext();

        List<ItemFeed> listaItens = new ArrayList<>();
        Date data = new Date();
        itemFeed = new ItemFeed(ITEM_ID, "titulo", "link Podcast", data.toString(), "descricao", "download url");
        // define um downloadID para ser recuperado no teste de leitura 
        itemFeed.setDownloadID(DOWNLOAD_ID);
        listaItens.add(itemFeed);
        // salva um item no bd inicialmente
        PodcastProviderHelper.saveItens(context, listaItens);
    }
```

### beforeTests()

Neste método atualizamos tudo que precisa ser inicializado antes de cada teste

```java

    @Before
    public void beforeTests() {
        // antes de cada teste atualiza o total de itens salvo no bd
        itensTotal = PodcastProviderHelper.getItens(context).size();
    }
```

### Inserção

Aqui testamos a inserção de um item no bd. Para validar, comparamos o total de itens salvos após a operação com o a quantidade antes do teste somada ao total inserido.

```java

    @Test
    public void insertTest() {
        // cria objeto a ser inserido
        ItemFeed itemFeed = new ItemFeed(ITEM_ID + 1, "title", "link", "date", "teste", "downloadLink");

        // insere objeto
        List<ItemFeed> lista = new ArrayList<>();
        lista.add(itemFeed);
        PodcastProviderHelper.saveItens(context, lista);
        
        // testa se a quantitade de itens salvos corresponde ao esperado
        assertEquals(lista.size() + itensTotal, PodcastProviderHelper.getItens(context).size());
    }
```

#### Correções

Após a execução desse teste, percebemos que o método PodcastProviderHelper.getItens estava incorreto, pois o cursor obtido do provider era percorrido num laço do tipo

```java
while (c.moveToNext()){
    [...]
}
```
O que excluia o primeiro item da lista recuperada. Com isso, o método foi corrigido para utilizar o laço

```java
if (c.getCount() > 0) {
    do{
        [...]
    } while (c.moveToNext());
}
```

### Busca

Aqui testamos a busca de um item no bd. Como possuímos dois métodos para recuperar os itens salvos, criamos dois testes. 

#### Busca simples

Aqui criamos um teste para verificar a busca de um único item salvo no método beforeClass(). Para validar, verificamos se o id do objeto recuperado é igual ao do objeto salvo no método inicial.

```java

    @Test
    public void leituraSimplesTest(){
        // recupera item salvo previamente
        ItemFeed item = PodcastProviderHelper.getItem(context, DOWNLOAD_ID);
        // testa se os ids sao iguais
        assertEquals(itemFeed.getId(), item.getId());
    }
```

#### Busca de lista

Aqui criamos um teste para verificar a busca de todos os itens salvos no BD. Para validar, verificamos se a lista recuperada tem a mesma quantidade de elementos salvo no BD (valor armazenado na varivel itensTotal, que é atualizada antes de cada teste).

```java

    @Test
    public void leituraListaTest(){
        // recupera itens salvos no BD
        List<ItemFeed> lista = PodcastProviderHelper.getItens(context);
        // testa se a lista tem o tamnho certo
        assertEquals(itensTotal, lista.size());
    }
```


### Atualização

Aqui testamos a atualização de um item no bd. Como possuímos três métodos para atualizar atributos dos itens salvos, criamos três testes. 

#### Atributo DownloadId

Aqui testamos o método PodcastProviderHelper.updateDownloadID(Context context, int podcastID, long downloadID) que atualiza o atributo DownloadId do item de id passado. Para validar, como buscamos apenas por esse mesmo atributo, verificamos se os Ids passados e buscados são iguais.

```java

    @Test
    public void updateDownloadIdTest(){
        // atualiza o download id
        PodcastProviderHelper.updateDownloadID(context, ITEM_ID, DOWNLOAD_ID + 1);
        //busca o item
        ItemFeed item = PodcastProviderHelper.getItem(context, DOWNLOAD_ID + 1);
        // verifica se os itens tem o mesmo ID
        assertEquals(ITEM_ID, item.getId());
    }
```

#### Atributo FileURI

Aqui testamos o método PodcastProviderHelper.updateFileURI(Context context, int podcastID, String fileURI) que atualiza o atributo FileURI do item de id passado. Para validar, verificamos se as strings passada e buscada são iguais.

```java
    
    @Test
    public void updateFileUriTest(){
        // atualiza a uri do podcast
        PodcastProviderHelper.updateFileURI(context, ITEM_ID, "new file uri");
        // busca o item
        ItemFeed item = PodcastProviderHelper.getItem(context, DOWNLOAD_ID);
        // verifica se o iten tem o mesmo fileURI
        assertEquals("new file uri", item.getFileURI());
    }
```

#### Atributo PlayedMsec

Aqui testamos o método PodcastProviderHelper.updatePlayedMsec(Context context, int podcastID, int playedMsec) que atualiza o atributo PlayedMsec do item de id passado. Para validar, verificamos se os inteiros passado e buscado são iguais.

```java

    @Test
    public void updatePlayedMsecTest(){
        // atualiza o valor de playedMsec
        PodcastProviderHelper.updatePlayedMsec(context, ITEM_ID, 100);
        // busca o item
        ItemFeed item = PodcastProviderHelper.getItem(context, DOWNLOAD_ID);
        // verifica se o iten tem o mesmo valor de playedMsec
        assertEquals(100, item.getPlayedMsec());
    }
```

## Integração (UI - Espresso)

Nesta etapa, foi definida uma suíte de testes feita eplo Espresso para avaliar integração e interface. 

### Espresso Test Recorder

Por motivos de facilidade e organização do código de testes gerado, foi utilizado a ferramenta provida pelo Android Studio, o Espresso Test Recorder.

![Alt espresso_option](Imgs/espresso_option.png)

Com esta ferramenta, nos é disponibilizado uma interface que registra as ações realizadas pelo emulador, tais como os cliques e acessos às diferentes componentes presentes no aplicativo, etc. 

### Descrição

Para cada interação feita com o emulador, é possível adicionar *assertions* que verificam se o que é disponibilizado pelo app é condizente com o que é esperado.
Baseado nas *assertions* definidas, é gerado automaticamente código correspondente aquilo, como pode ser visto abaixo pela imagem descrevendo a interface do Espresso Test Recorder e o código gerado baseado na *assertion* feita.

![Alt espresso_option](Imgs/espresso_assertion.PNG)

```java

    DataInteraction linearLayout = onData(anything())
            .inAdapterView(allOf(withId(R.id.items),
                    childAtPosition(
                            withClassName(is("android.widget.LinearLayout")),
                            0)))
            .atPosition(0);
    linearLayout.perform(click());

    ViewInteraction textView = onView(
            allOf(withId(R.id.podcastitle), withText("O Homem foi mesmo até a Lua?"),
                    childAtPosition(
                            childAtPosition(
                                    withId(android.R.id.content),
                                    0),
                            0),
                    isDisplayed()));
    textView.check(matches(withText("O Homem foi mesmo até a Lua?")));
```

A suite de testes elaborada envolve os 2 primeiros itens da lista de episódios de podcast.
As ações escolhidas sequencialmente para a nossa suite de testes de integração e interface foram (todas os trechos de código abaixo são referentes ao segundo item da lista de podcasts, em que o episódio correspondente é o de *Darwin e a Evolução*):

1) Verificar informações dos itens da lista de episódios disponibilizadas na MainActivity.

```java

    ViewInteraction linearLayout3 = onView(
            allOf(childAtPosition(
                    allOf(withId(R.id.items),
                            childAtPosition(
                                    IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class),
                                    0)),
                    1),
                    isDisplayed()));
    linearLayout3.check(matches(isDisplayed()));

    ViewInteraction textView7 = onView(
            allOf(withId(R.id.item_title), withText("Darwin e a Evolução"),
                    childAtPosition(
                            childAtPosition(
                                    IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class),
                                    0),
                            0),
                    isDisplayed()));
    textView7.check(matches(withText("Darwin e a Evolução")));

    ViewInteraction textView8 = onView(
            allOf(withId(R.id.item_date), withText("Mon, 21 Jun 2010 10:45:05 GMT"),
                    childAtPosition(
                            childAtPosition(
                                    withId(R.id.items),
                                    1),
                            1),
                    isDisplayed()));
    textView8.check(matches(withText("Mon, 21 Jun 2010 10:45:05 GMT")));
```

2) Realizar clique em itens da lista de episódios de podcast.

```java

    DataInteraction linearLayout4 = onData(anything())
            .inAdapterView(allOf(withId(R.id.items),
                    childAtPosition(
                            withClassName(is("android.widget.LinearLayout")),
                            0)))
            .atPosition(1);
    linearLayout4.perform(click());
```

3) Verificar as informações que são disponibilizadas pela nova activity carregada (EpisodeDetailActivity).

```java

    ViewInteraction textView9 = onView(
            allOf(withId(R.id.podcastitle), withText("Darwin e a Evolução"),
                    childAtPosition(
                            childAtPosition(
                                    withId(android.R.id.content),
                                    0),
                            0),
                    isDisplayed()));
    textView9.check(matches(withText("Darwin e a Evolução")));

    ViewInteraction textView10 = onView(
            allOf(withId(R.id.pubDate), withText("Mon, 21 Jun 2010 10:45:05 GMT"),
                    childAtPosition(
                            childAtPosition(
                                    withId(android.R.id.content),
                                    0),
                            1),
                    isDisplayed()));
    textView10.check(matches(withText("Mon, 21 Jun 2010 10:45:05 GMT")));

    ViewInteraction textView11 = onView(
            allOf(withId(R.id.description), withText("Programa 3"),
                    childAtPosition(
                            childAtPosition(
                                    withId(android.R.id.content),
                                    0),
                            2),
                    isDisplayed()));
    textView11.check(matches(withText("Programa 3")));
```

4) Retornar a MainActivity e verificar a existência do botão de download do episódio antes de realizar o download.

```java

    pressBack();

    ViewInteraction button8 = onView(
            allOf(withId(R.id.item_action),
                    childAtPosition(
                            childAtPosition(
                                    IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class),
                                    0),
                            1),
                    isDisplayed()));
    button8.check(matches(isDisplayed()));

    ViewInteraction button9 = onView(
            allOf(withId(R.id.item_action), withText("BAIXAR"),
                    childAtPosition(
                            childAtPosition(
                                    withClassName(is("android.widget.LinearLayout")),
                                    0),
                            1),
                    isDisplayed()));
    button9.perform(click());
```

5) Realizar clique, quando o download do episódio é finalizado, em botão que tem seu texto modificado de **BAIXAR** para **REPRODUZIR**.

```java

    ViewInteraction button10 = onView(
            allOf(withId(R.id.item_action), withText("REPRODUZIR"),
                    childAtPosition(
                            childAtPosition(
                                    withClassName(is("android.widget.LinearLayout")),
                                    0),
                            1),
                    isDisplayed()));
    button10.perform(click());
```

6) Verificar informações em nova activity (PlayActivity) carregada do episódio que se deseja reproduzir o aúdio.

```java

    ViewInteraction textView12 = onView(
            allOf(withId(R.id.playtitle), withText("Darwin e a Evolução"),
                    childAtPosition(
                            childAtPosition(
                                    withId(android.R.id.content),
                                    0),
                            0),
                    isDisplayed()));
    textView12.check(matches(withText("Darwin e a Evolução")));
```

7) Verificar a existência dos botões de **PLAY** e **PAUSE** na PlayActivity.

```java

    ViewInteraction button11 = onView(
            allOf(withId(R.id.play),
                    childAtPosition(
                            childAtPosition(
                                    IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class),
                                    1),
                            0),
                    isDisplayed()));
    button11.check(matches(isDisplayed()));

    ViewInteraction button12 = onView(
            allOf(withId(R.id.pause),
                    childAtPosition(
                            childAtPosition(
                                    IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class),
                                    1),
                            1),
                    isDisplayed()));
    button12.check(matches(isDisplayed()));
```

8) Realizar clique nos botões de **PLAY** e **PAUSE**.

```java

    ViewInteraction button13 = onView(
            allOf(withId(R.id.play), withText("PLAY"),
                    childAtPosition(
                            childAtPosition(
                                    withClassName(is("android.widget.LinearLayout")),
                                    1),
                            0),
                    isDisplayed()));
    button13.perform(click());

    ViewInteraction button14 = onView(
            allOf(withId(R.id.pause), withText("PAUSE"),
                    childAtPosition(
                            childAtPosition(
                                    withClassName(is("android.widget.LinearLayout")),
                                    1),
                            1),
                    isDisplayed()));
    button14.perform(click());
```

## LeakCanary

### Análise

Como forma adicional para avaliar os tópicos listados como requisitos para o projeto, foi utilizado o LeakCanary para verificar possíveis vazamentos de memória do aplicativo.

Foi seguido o passo a passo do [Github do LeakCanary](https://github.com/square/leakcanary) para acoplá-lo ao aplicativo.

O aplicativo foi rodado com build *debug* e não foi notado nenhum vazamento de memória. Foram feitas interações com o aplicativo dos tipos:

1) Acesso às informações dos episódios de podcast.

2) Download de podcast.

3) Reprodução dos áudios de podcasts.

4) Acesso ao SettingsActivity.

### Justificativa

Uma possível explicação para a ausência de vazamento de memória seria o fato de grande parte das variáveis utilizadas serem locais e aquelas que não são deste tipo submetem-se a verificações que previnem a ocorrência de vazamento.