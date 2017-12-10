package br.ufpe.cin.if710.podcast.db;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.ufpe.cin.if710.podcast.domain.ItemFeed;

import static org.junit.Assert.assertEquals;

/**
 * Created by Beto on 09/12/2017.
 */
@RunWith(AndroidJUnit4.class)
public class JUnitTestSuite {

    private static final long DOWNLOAD_ID = 10;
    private static final int ITEM_ID = 1;
    static Context context;
    static ItemFeed itemFeed;
    int itensTotal;

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

    @Before
    public void beforeTests() {
        // antes de cada teste atualiza o total de itens salvo no bd
        itensTotal = PodcastProviderHelper.getItens(context).size();
    }

    @After
    public void afterTests(){
        PodcastProviderHelper.updateDownloadID(context, ITEM_ID, DOWNLOAD_ID);
    }

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


    @Test
    public void leituraSimplesTest(){
        // recupera item salvo previamente
        ItemFeed item = PodcastProviderHelper.getItem(context, DOWNLOAD_ID);
        // testa se os ids sao iguais
        assertEquals(itemFeed.getId(), item.getId());
    }

    @Test
    public void leituraListaTest(){
        // recupera itens salvos no BD
        List<ItemFeed> lista = PodcastProviderHelper.getItens(context);
        // testa se a lista tem o tamnho certo
        assertEquals(itensTotal, lista.size());
    }


    @Test
    public void updateDownloadIdTest(){
        // atualiza o download id
        PodcastProviderHelper.updateDownloadID(context, ITEM_ID, DOWNLOAD_ID + 1);
        //busca o item
        ItemFeed item = PodcastProviderHelper.getItem(context, DOWNLOAD_ID + 1);
        // verifica se os itens tem o mesmo ID
        assertEquals(ITEM_ID, item.getId());
    }

    @Test
    public void updateFileUriTest(){
        // atualiza a uri do podcast
        PodcastProviderHelper.updateFileURI(context, ITEM_ID, "new file uri");
        // busca o item
        ItemFeed item = PodcastProviderHelper.getItem(context, DOWNLOAD_ID);
        // verifica se o iten tem o mesmo fileURI
        assertEquals("new file uri", item.getFileURI());
    }

    @Test
    public void updatePlayedMsecTest(){
        // atualiza o valor de playedMsec
        PodcastProviderHelper.updatePlayedMsec(context, ITEM_ID, 100);
        // busca o item
        ItemFeed item = PodcastProviderHelper.getItem(context, DOWNLOAD_ID);
        // verifica se o iten tem o mesmo valor de playedMsec
        assertEquals(100, item.getPlayedMsec());
    }

}
