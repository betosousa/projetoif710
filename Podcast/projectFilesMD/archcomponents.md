# Architecture Components

## Room

### Entity

Para refatorar a aplicação para o uso de Room, primeiro anotamos a classe ItemFeed com @Entity para representar uma tabela. Anotamos também seu atributo  'int id' com @PrimaryKey para marcá-lo como id da tabela e seus demais atributos com @ColumnInfo para marcá-los como colunas. Como a classe possui dois construtores, anotamos um deles com @Ignored para evitar erros de build.

```java

@Entity
public class ItemFeed implements Serializable {
    @PrimaryKey
    private final int id;
    @ColumnInfo
    private final String title;
    @ColumnInfo
    private final String link;
    @ColumnInfo
    private final String pubDate;
    @ColumnInfo
    private final String description;
    @ColumnInfo
    private final String downloadLink;
    @ColumnInfo(name = "download_id")
    private long downloadID;
    @ColumnInfo
    private String fileURI;
    @ColumnInfo
    private int playedMsec;

    public ItemFeed(int id, String title, String link, String pubDate, String description, String downloadLink) {
        this.id = id;
        this.title = title;
        this.link = link;
        this.pubDate = pubDate;
        this.description = description;
        this.downloadLink = downloadLink;
    }

    @Ignore
    public ItemFeed(String title, String link, String pubDate, String description, String downloadLink) {
        this.id = -1;
        this.title = title;
        this.link = link;
        this.pubDate = pubDate;
        this.description = description;
        this.downloadLink = downloadLink;
    }

    // Getters and setters ...
}
```

### DAO

Para acessar e manusear os objetos ItemFeed, precisamos criar uma interface com os métodos necessários para tal. Criamos a interface ItemFeedDao, anotada com @Dao. Os métodos insertAll e updateItem foram anotados com @Insert e @Update respectivamente, para que o processador de anotações de Room crie as implementações concretas. Os demais métodos foram anotados com @Query e executam as querys defindas em cada um.

```java

@Dao
public interface ItemFeedDao {
    @Insert
    void insertAll(List<ItemFeed> itens);

    @Update
    void updateItem(ItemFeed itemFeed);

    @Query("SELECT * FROM ItemFeed")
    List<ItemFeed> getAll();

    @Query("SELECT * FROM ItemFeed WHERE id LIKE :id")
    ItemFeed findById(int id);

    @Query("SELECT * FROM ItemFeed WHERE download_id LIKE :downloadID")
    ItemFeed findByDownloadId(long downloadID);
}
```

### Database

Para utilizar Room, precisamos  definir uma classe abstrata que represente o banco de dados. Assim, criamos a classe ItemFeedDB, onde definimos as entidades utilizadas (no caso, apenas ItemFeed) e a assinatura do método que retorna o Dao a ser utilizado (no caso ItemFeedDao).

```java

@Database(entities = {ItemFeed.class}, version = 1)
public abstract class ItemFeedDB extends RoomDatabase {
    public abstract ItemFeedDao itemFeedDao();
}
```

### Ajustes realizados

Após os passos anteriores, podemos agora refatorar o app que antes utilizava Content Providers e SQLite para utilizar apenas Room.

Adaptamos a classe PodcastProviderHelper que concentrava todos os acessos ao Content Provider para utilizar Room. Através do padrão singleton definimos o atributo static ItemFeedDB db e o método getDao(Context) para acessar o ItemFeedDao

```java

    private static ItemFeedDB db;

    private static ItemFeedDao getDao(Context context) {
        if (db == null) {
            Room.databaseBuilder(context, ItemFeedDB.class, "database-name").build();
        }
        return db.itemFeedDao();
    }
```

Dessa forma, podemos reescrever os métodos da classe para utilizar Room com algumas poucas linhas

```java

    public static List<ItemFeed> getItens(Context context){
        return getDao(context).getAll();
    }

    public static void updateDownloadID(Context context, int podcastID, long downloadID){
        ItemFeed itemFeed = getDao(context).findById(podcastID);
        itemFeed.setDownloadID(downloadID);
        getDao(context).updateItem(itemFeed);
    }

    public static ItemFeed getItem(Context context, long downloadID){
        return getDao(context).findByDownloadId(downloadID);
    }

    public static void updateFileURI(Context context, int podcastID, String fileURI){
        ItemFeed itemFeed = getDao(context).findById(podcastID);
        itemFeed.setFileURI(fileURI);
        getDao(context).updateItem(itemFeed);
    }

    public static void saveItens(Context context, List<ItemFeed> itemList) {
        getDao(context).insertAll(itemList);
    }

    public static void updatePlayedMsec(Context context, int podcastID, int playedMsec){
        ItemFeed itemFeed = getDao(context).findById(podcastID);
        itemFeed.setPlayedMsec(playedMsec);
        getDao(context).updateItem(itemFeed);
    }
```

## LiveData

### Caso de Uso

Ao analisar a [documentação do LiveDatã](https://developer.android.com/topic/libraries/architecture/livedata.html), para o caso de uso que emprega LiveData, foi definido a possibilidade de aplicar LiveData envolvendo a atualização da lista de podcasts e o JobScheduler. Visto que o LiveData apresenta funcionalidade semelhante ao Padrão de Projeto: *Observer*, isto é, observadores de determinada informação /objeto são atualizados quando este é atualizado, no nosso caso, as *activities* que disponibilizam as informações dos podcasts seriam atualizadas assim que houvesse atualização feita pelo JobScheduler.