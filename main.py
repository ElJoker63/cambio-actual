import flet as ft
from api_toque import get_compra, get_venta, get_msg, get_crypto


APP_NAME = "Cambio actual"
VERSION = '0.0.1'


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
                                        f"Los valores de REFERENCIA que aquí se muestran son el resultado del cálculo de la mediana de los números escritos en ofertas de compra y venta de divisas registradas por nuestro sistema automatizado en sitios web de clasificados y grupos de redes sociales. No son operaciones concretadas,a sino expresiones «deseos» de los actores del mercado informal. El precio final de la compraventa entre privados puede y suele variar. Insistimos en que los números que mostramos no son la tasa OFICIAL, sino que deben ser tomados solo como REFERENCIA.", color=ft.colors.RED, size=10,
                                        font_family="Qs-M",
                                    ),
                                ),
                            ]
                        ),
                        padding=0,
                    )
                )

        def handle_close(e):
            self.page.close(self.dlg_modal)

        self.dlg_modal = ft.CupertinoAlertDialog(
            modal=True,
            title=ft.Text("INFORMACIÓN", color=ft.colors.PRIMARY),
            content=ft.Column(
                controls=[
                    ft.Text('Aplicación que brinda la información de los precios proporcionados por ElToque del cambio actual en tiempo real.', color=ft.colors.PRIMARY, text_align='center'),
                    ft.Divider(height=40, color=ft.colors.TRANSPARENT),
                    ft.Row([ft.Text('Copyright (c) 2024 AEWareDevs', color=ft.colors.PRIMARY)], alignment=ft.MainAxisAlignment.CENTER),
                    ft.Row([ft.Text(f'Version: v{VERSION}', color=ft.colors.PRIMARY)], alignment=ft.MainAxisAlignment.CENTER),
                    ft.Row([
                        ft.TextButton(content=
                                    ft.Row([
                                        ft.Image(src="http://192.168.1.2:5500/assets/Octocat.svg", height=24, width=24, color=ft.colors.PRIMARY),
                                        ft.Text("GitHub")]),url=(f"https://github.com/ElJoker63/cambio-actual")),
                        ft.TextButton(content=
                                    ft.Row([
                                        ft.Image(src="http://192.168.1.2:5500/assets/Telegram.svg", height=24, width=24, color=ft.colors.PRIMARY),
                                        ft.Text("Telegram")]),url=(f"https://t.me/ElJoker63"))], alignment=ft.MainAxisAlignment.CENTER)
                ],
                tight=True,
                spacing=0,
            ),
            actions=[
                ft.TextButton("CERRAR", on_click=handle_close),
            ],
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

        self.update_coin_list()
        self.update_msg_list()

        self.page.on_route_change = self.route_change
        self.page.on_view_pop = self.view_pop
        self.page.go(self.page.route)

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
                                    ),
                                    title=ft.Text(
                                        f"{coin if coin != 'ECU' else 'EURO'}",
                                        font_family="Qs-B",
                                    ),
                                    subtitle=ft.Text(
                                        f"COMPRA: {compra} | VENTA: {venta}",
                                        font_family="Qs-M",
                                    ),
                                ),
                            ]
                        ),
                        padding=0,
                    )
                )
            )
        # self.xcoin.update()

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
                                        f"http://192.168.1.2:5500/assets/{ms['symbol']}.png"
                                    ),
                                    title=ft.Text(f"{ms['symbol']}"),
                                    subtitle=ft.Text(
                                        f"{ms['price']}", font_family="Qs-M"
                                    ),
                                ),
                            ]
                        ),
                        padding=0,
                    )
                )
            )
        # self.msg.update()

    def route_change(self, route):
        self.page.views.clear()
        self.page.views.append(
            ft.View(
                "/",
                [
                    ft.AppBar(
                        title=ft.Text(APP_NAME, font_family="Qs-B"),
                        actions=[
                            ft.IconButton(
                                ft.icons.CURRENCY_EXCHANGE_ROUNDED,
                                icon_color=ft.colors.WHITE,
                                on_click=lambda _: self.page.go("/crypto"),
                            ),
                            ft.IconButton(
                                ft.icons.INFO_OUTLINE_ROUNDED,
                                icon_color=ft.colors.WHITE,
                                on_click=lambda e: self.page.open(self.dlg_modal),
                            ),
                        ],
                        bgcolor=ft.colors.PRIMARY,
                    ),
                    self.xcoin,
                    self.reference,
                ],
            )
        )
        if self.page.route == "/crypto":
            self.page.views.append(
                ft.View(
                    "/crypto",
                    [
                        ft.AppBar(
                            title=ft.Text("Cryptocoins", font_family="Qs-B"),
                            actions=[
                                ft.IconButton(
                                    ft.icons.HOME_OUTLINED,
                                    icon_color=ft.colors.WHITE,
                                    on_click=lambda _: self.page.go("/"),
                                ),
                                ft.IconButton(
                                    ft.icons.INFO_OUTLINE_ROUNDED,
                                    icon_color=ft.colors.WHITE,
                                    on_click=lambda e: self.page.open(self.dlg_modal),
                                ),
                            ],
                            bgcolor=ft.colors.PRIMARY,
                        ),
                        self.msg,
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
