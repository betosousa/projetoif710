package br.ufpe.cin.if710.podcast.db;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

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

    static Context context;



    int itensTotal;

    @BeforeClass
    public static void beforeClass() {
        // recupera o contexto do app
        context = InstrumentationRegistry.getTargetContext();

        List<ItemFeed> listaItens = new ArrayList<>();
        Date data = new Date();
        listaItens.add(new ItemFeed(1, "titulo", "link Podcast", data.toString(), "descricao", "download url"));
        // salva um item no bd inicialmente
        PodcastProviderHelper.saveItens(context, listaItens);
    }

    @Before
    public void beforeTests() {
        // antes de cada teste atualiza o total de itens salvo no bd
        itensTotal = PodcastProviderHelper.getItens(context).size();
    }

    @Test
    public void insertTest() {
        // cria objeto a ser inserido
        ItemFeed itemFeed = new ItemFeed( "title", "link", "date", "teste", "downloadLink");

        // insere objeto
        List<ItemFeed> lista = new ArrayList<>();
        lista.add(itemFeed);
        PodcastProviderHelper.saveItens(context, lista);

        // testa se a quantitade de itens salvos corresponde ao esperado
        assertEquals(lista.size() + itensTotal, PodcastProviderHelper.getItens(context).size());
    }



}
