# Memória

A análise ds testes foi realizada com um emulador do Nexus 4 utilizando a API 25.

Pelo que se pode perceber, o aplicativo usa em média 13 MB de memória, quando em primeiro plano com a MainActivity. Quando em background, o aplicativo usa em média blablabla

[//]: <> (inserir possível imagem)

Ao acessar a activity que disponibiliza as informações do episódio do podcast (EpisodeDetailActivity), não há modificações significantes no uso da memória. Porém ao retornar a MainActivity, há um leve incremento no uso da memória, possivelmente explicado pelo carregamento desta junto a remoção da EpisodeDetailActivity previamente carregada.

Este mesmo comportamento é visto também no PlayActivity, que é uma activity chamada quando o usuário deseja reproduzir o episódio baixado.