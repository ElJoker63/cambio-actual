import requests
import json
from datetime import datetime, timedelta

'''API DE ELTOQUE MEDIANTE REQUESTS DIRECTAS
CREADA POR ELJOKER63
DISPONIBLE PARA TODOS'''

fecha_actual = datetime.now()
fecha_24h_antes = fecha_actual - timedelta(days=1)

# Formatear las fechas en el formato deseado
fecha_actual_formateada = fecha_actual.strftime("%Y-%m-%d")
fecha_24h_antes_formateada = fecha_24h_antes.strftime("%Y-%m-%d")

def get_compra(coin):
    url = f'https://api.cambiocuba.money/api/v1/x-rates-by-date-range-history?trmi=true&cur={coin}&offer=Compra&period=2D'
    req = requests.get(url).text
    res = json.loads(req)
    last = res[0]['last']
    return last['value']
    

def get_venta(coin):
    url = f'https://api.cambiocuba.money/api/v1/x-rates-by-date-range-history?trmi=true&cur={coin}&offer=Venta&period=2D'
    req = requests.get(url).text
    res = json.loads(req)
    if res == []:
        last = {'value': 0, 'date': ''}
    else:
        last = res[0]['last']
    return last['value']

def get_msg():
    url = f'https://api.cambiocuba.money/api/v1/x-rates?token=aCY78gC3kWRv1pR7VfgSlg&date_from={fecha_24h_antes_formateada}%2000:00:00&date_to={fecha_actual_formateada}%2023:59:59&offer=Venta'
    req = requests.get(url).text
    res = json.loads(req)
    return res

def historial():
    url = f'https://api.cambiocuba.money/api/v1/x-rates-by-date-range-history?trmi=true&cur=USD&period=7D'
    req = requests.get(url).text
    res = json.loads(req)
    return res

def get_crypto():
    url = f'https://api.cambiocuba.money/api/v1/crypto-market-listings-latest'
    req = requests.get(url).text
    res = json.loads(req)
    return res

#lol = historial()
#for lo in lol:
    #print(lo)