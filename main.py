import flet as ft
from api_toque import get_compra, get_venta, get_msg, get_crypto
from api import *

APP_NAME = "CAMBIO ACTUAL"
VERSION = '0.0.2'

class CoinExchangeApp:
    def __init__(self):
        self.page = None
        self.xcoin = ft.ListView(expand=True, auto_scroll=False)
        self.msg = ft.ListView(expand=True, auto_scroll=False)
        
        self.reference = ft.Card(
            content=ft.Container(
                content=ft.Column(
                    [
                        ft.ListTile(
                            subtitle=ft.Text(
                                f"Los valores de REFERENCIA que aquí se muestran son el resultado del cálculo de la mediana de los números escritos en ofertas de compra y venta de divisas registradas por nuestro sistema automatizado en sitios web de clasificados y grupos de redes sociales. No son operaciones concretadas,a sino expresiones «deseos» de los actores del mercado informal. El precio final de la compraventa entre privados puede y suele variar. Insistimos en que los números que mostramos no son la tasa OFICIAL, sino que deben ser tomados solo como REFERENCIA.",
                                color=ft.colors.RED,
                                size=10,
                                font_family="Qs-M",
                            ),
                        ),
                    ]
                ),
                padding=0,
            )
        )
        
        self.update_modal = ft.CupertinoAlertDialog(
            modal=True,
            title=ft.Text(f"ACTUALIZACIÓN {get_update()[2]}", font_family="Qs-B", size=20, color=ft.colors.PRIMARY),
            content=ft.Column(
                controls=[
                    ft.Divider(height=40, color=ft.colors.TRANSPARENT),
                    ft.Row([ft.Image(src="http://192.168.1.2:5500/assets/Update.svg", width=35, color=ft.colors.PRIMARY)], alignment=ft.MainAxisAlignment.CENTER),
                    ft.Row([ft.Text(f'Descargas: {get_update()[4]}', color=ft.colors.PRIMARY)], alignment=ft.MainAxisAlignment.CENTER),
                    ft.Divider(height=40, color=ft.colors.TRANSPARENT),
                    ft.Markdown(get_update()[1]),
                    ft.Divider(height=40, color=ft.colors.TRANSPARENT),
                ],
                tight=True,
                spacing=0,
            ),
            actions=[
                ft.TextButton("ACTUALIZAR", url=get_update()[3]),
            ],
        )

        def handle_close(e):
            self.page.close(self.dlg_modal)

        self.dlg_modal = ft.CupertinoAlertDialog(
            modal=True,
            title=ft.Text("INFORMACIÓN", color=ft.colors.PRIMARY),
            content=ft.Column(
                controls=[
                    ft.Divider(height=40, color=ft.colors.TRANSPARENT),
                    ft.Text('Aplicación que brinda la información de los precios proporcionados por ElToque del cambio actual en tiempo real.', color=ft.colors.PRIMARY, text_align='center'),
                    ft.Divider(height=40, color=ft.colors.TRANSPARENT),
                    ft.Row([ft.Text('Copyright (c) 2024', color=ft.colors.PRIMARY)], alignment=ft.MainAxisAlignment.CENTER),
                    ft.Row([ft.Text('AEWareDevs & ByteBloom', color=ft.colors.PRIMARY)], alignment=ft.MainAxisAlignment.CENTER),
                    ft.Row([ft.Text(f'Version: v{VERSION}', color=ft.colors.PRIMARY)], alignment=ft.MainAxisAlignment.CENTER),
                    ft.Divider(height=40, color=ft.colors.TRANSPARENT),
                    ft.Row([
                        ft.TextButton(content=
                                    ft.Row([
                                        ft.Image(src="http://192.168.1.2:5500/assets/Octocat.svg", height=24, width=24, color=ft.colors.PRIMARY),
                                        ft.Text("GitHub",font_family="Qs-B")]),url=(f"https://github.com/ElJoker63/cambio-actual")),
                        ft.TextButton(content=
                                    ft.Row([
                                        ft.Image(src="http://192.168.1.2:5500/assets/Telegram.svg", height=24, width=24, color=ft.colors.PRIMARY),
                                        ft.Text("Telegram",font_family="Qs-B")]),url=(f"https://t.me/ElJoker63"))], alignment=ft.MainAxisAlignment.CENTER),
                    ft.Row([ft.TextButton(content=
                                    ft.Row([
                                        ft.Image(src="http://192.168.1.2:5500/assets/lol.png", height=24, width=24, color=ft.colors.PRIMARY),
                                        ft.Text("ByteBloom",font_family="Qs-B")]),url=(f"https://t.me/+z771oePMvc44NDI5"))], alignment=ft.MainAxisAlignment.CENTER),
                ],
                tight=True,
                spacing=0,
            ),
            actions=[
                ft.TextButton("CERRAR", on_click=handle_close),
            ],
        )
        self.wv = ft.WebView("https://img.cambiocuba.money/calculator/fiat", expand=True)

    def create_app_bar(self, title):
        def handle_popup_item_clicked(e):
            route = e.control.data
            if route:
                self.page.go(route)

        # Definir los elementos del menú según la ruta actual
        menu_items = []
        current_route = self.page.route

        # Siempre incluir "INICIO" si no estamos en la ruta principal
        if current_route != "/":
            menu_items.append(
                ft.PopupMenuItem(
                    text="INICIO",
                    data="/",
                    on_click=handle_popup_item_clicked
                )
            )

        # Añadir "CRYPTOCOINS" si no estamos en esa vista
        if current_route != "/crypto":
            menu_items.append(
                ft.PopupMenuItem(
                    text="CRYPTOCOINS",
                    data="/crypto",
                    on_click=handle_popup_item_clicked
                )
            )

        # Añadir "CONVERTIDOR" si no estamos en esa vista
        if current_route != "/calc":
            menu_items.append(
                ft.PopupMenuItem(
                    text="CONVERTIDOR",
                    data="/calc",
                    on_click=handle_popup_item_clicked
                )
            )

        # Siempre incluir "INFORMACION" al final
        menu_items.append(
            ft.PopupMenuItem(
                text="INFORMACION",
                on_click=lambda _: self.page.open(self.dlg_modal)
            )
        )

        return ft.AppBar(
            title=ft.Row([ft.Text(title, color=ft.colors.WHITE, font_family="Qs-B")], alignment=ft.MainAxisAlignment.CENTER),
            actions=[
                ft.PopupMenuButton(
                    icon_color="#ffffff",
                    items=menu_items
                ),
            ],
            bgcolor=ft.colors.PRIMARY,
        )

    def main(self, page: ft.Page):
        self.page = page
        self.page.title = APP_NAME
        self.page.theme_mode = ft.ThemeMode.DARK
        self.page.scroll = ft.ScrollMode.HIDDEN
        self.page.theme = ft.Theme(
            font_family="Qs-L",
            color_scheme=ft.ColorScheme(
                primary=ft.colors.GREEN,
                secondary=ft.colors.GREEN_ACCENT,
                background=ft.colors.SECONDARY,
            ),
        )
        self.page.fonts = {
            "Qs-B": "http://192.168.1.2:5500/assets/fonts/Quicksand-Bold.ttf",
            "Qs-L": "http://192.168.1.2:5500/assets/fonts/Quicksand-Light.ttf",
            "Qs-M": "http://192.168.1.2:5500/assets/fonts/Quicksand-Medium.ttf",
            "Qs-R": "http://192.168.1.2:5500/assets/fonts/Quicksand-Regular.ttf",
            "Qs-SB": "http://192.168.1.2:5500/assets/fonts/Quicksand-SemiBold.ttf",
        }

        self.page.client_storage.set("coins", ["USD", "ECU", "MLC", "BTC"])
        self.float_button = ft.FloatingActionButton(icon=ft.icons.UPDATE, on_click=lambda _: self.page.update(), bgcolor=ft.colors.PRIMARY)

        self.update_coin_list()
        self.update_msg_list()

        self.page.on_route_change = self.route_change
        self.page.on_view_pop = self.view_pop
        self.page.go("/")

    def update_coin_list(self):
        self.xcoin.controls.clear()
        for coin in self.page.client_storage.get("coins"):
            compra = get_compra(coin)
            venta = get_venta(coin)
            self.xcoin.controls.append(
                ft.Card(
                    content=ft.Container(
                        content=ft.Column(
                            [
                                ft.ListTile(
                                    leading=ft.Image(
                                        src=f"http://192.168.1.2:5500/assets/{coin}.png",
                                        width=48,
                                        height=48,
                                        color=ft.colors.PRIMARY,
                                    ),
                                    title=ft.Text(
                                        f"{coin if coin != 'ECU' else 'EURO'}",
                                        font_family="Qs-B",
                                        color=ft.colors.PRIMARY,
                                    ),
                                    subtitle=ft.Text(
                                        f"COMPRA: {compra} | VENTA: {venta}",
                                        font_family="Qs-M",
                                        color=ft.colors.PRIMARY,
                                    ),
                                ),
                            ]
                        ),
                        padding=0,
                    )
                )
            )

    def update_msg_list(self):
        self.msg.controls.clear()
        msg = get_crypto()
        for ms in msg["crypto_market_cap"]:
            self.msg.controls.append(
                ft.Card(
                    content=ft.Container(
                        content=ft.Column(
                            [
                                ft.ListTile(
                                    leading=ft.Image(
                                        f"http://192.168.1.2:5500/assets/{ms['symbol']}.png",
                                        color=ft.colors.PRIMARY,
                                    ),
                                    title=ft.Text(
                                        f"{ms['symbol']}",
                                        color=ft.colors.PRIMARY,
                                        font_family="Qs-B"
                                    ),
                                    subtitle=ft.Text(
                                        f"{round(ms['price'], 2)}",
                                        font_family="Qs-M", 
                                        color=ft.colors.PRIMARY,
                                    ),
                                ),
                            ]
                        ),
                        padding=0,
                    )
                )
            )

    def route_change(self, route):
        update_info = get_update()
        current_version = VERSION
        latest_version = update_info[2].replace('v', '')

        if compare_versions(current_version, latest_version) == -1:
            self.page.open(self.update_modal)

        self.page.views.clear()
        
        # Vista principal
        if self.page.route == "/" or not self.page.route:
            self.page.views.append(
                ft.View(
                    "/",
                    [
                        self.create_app_bar(APP_NAME),
                        self.xcoin,
                        self.reference,
                    ],
                )
            )
        
        # Vista de crypto
        elif self.page.route == "/crypto":
            self.page.views.append(
                ft.View(
                    "/crypto",
                    [
                        self.create_app_bar("CRYPTOCOINS"),
                        self.msg,
                    ],
                )
            )
        
        # Vista de CONVERTIDOR
        elif self.page.route == "/calc":
            self.page.views.append(
                ft.View(
                    "/calc",
                    [
                        self.create_app_bar("CONVERTIDOR"),
                        self.wv,
                    ],
                )
            )

        self.page.update()

    def view_pop(self, view):
        self.page.views.pop()
        top_view = self.page.views[-1]
        self.page.go(top_view.route)


if __name__ == "__main__":
    app = CoinExchangeApp()
    ft.app(target=app.main, assets_dir="assets")