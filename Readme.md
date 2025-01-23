ZADANIE:
Należy stworzyć aplikację pobierającą kursy kryptowalut i umożliwiającą ich wymianę.

## Oczekiwanie działanie
Aplikacja będzie udostępniać endpointy, pod które będziemy wysyłać żadania, a odpowiedzi będą
zwracane w formacie JSON.

## Do zaimplementowania:
- Zwróć listę notowań dla podanej kryptowaluty
  Request: GET /currencies/{currency}?filter= - filter jest opcjonalną zmienną, która ogranicza listę
  notowań.
  Example request: /currencies/BTC?filter[]=USDT&filter[]=ETH
  Example response:
  {
  "source":"BTC",
  "rates":{
  "USDT":0.3321,
  "ETH":0.2911
  }
  }

- Umożliwić prognozę wymiany kryptowaluty A na B, w podanej ilości po aktualnym kursie.
  Do prognozy ma być doliczona prowizja w wielkości 1% od waluty, z której jest przesyłana.
  Request:POST /currencies/exchange
  RequestBody {"from":"currencyA","to":{"currencyB","currencyC"},"amount":121}.

Example response:
{
"from": "currencyA",
"currencyB":{
"rate":0.21,

"amount":121,
"result":0.213,
"fee":0.0001

},
"currencyC":{
"rate":0.21,

"amount":121,
"result":0.213,
"fee":0.0001

}
}

## Wymagania
- Wykonać integrację z dowolnym api, które udostępnia kursy kryptowalut.
- Implementacja w języku Java lub Scala.
- Sprawdzić poprawność rozwiązania.
  *Lista przewalutowań wyliczona w wątkach, będzie dodatkowym atutem.