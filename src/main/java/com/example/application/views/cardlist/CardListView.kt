package com.example.application.views.cardlist

import java.text.NumberFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Arrays
import java.util.Locale

import org.apache.commons.lang3.StringUtils

import com.vaadin.flow.component.combobox.ComboBox
import com.vaadin.flow.component.datepicker.DatePicker
import com.vaadin.flow.component.dependency.CssImport
import com.vaadin.flow.component.dependency.JsModule
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.grid.Grid.SelectionMode
import com.vaadin.flow.component.grid.GridVariant
import com.vaadin.flow.component.grid.HeaderRow
import com.vaadin.flow.component.gridpro.GridPro
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.html.Image
import com.vaadin.flow.component.html.Span
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.provider.ListDataProvider
import com.vaadin.flow.data.renderer.ComponentRenderer
import com.vaadin.flow.data.renderer.LocalDateRenderer
import com.vaadin.flow.data.renderer.NumberRenderer
import com.vaadin.flow.data.value.ValueChangeMode
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route
import com.example.application.views.main.MainView
import com.vaadin.flow.function.ValueProvider

@Route(value = "card-list", layout = MainView::class)
@PageTitle("Card List")
@CssImport(value = "./styles/views/cardlist/card-list-view.css", include = "lumo-badge")
@JsModule("@vaadin/vaadin-lumo-styles/badge.js")
class CardListView : Div() {

    private var grid: GridPro<Client>? = null
    private var dataProvider: ListDataProvider<Client>? = null

    private var idColumn: Grid.Column<Client>? = null
    private var clientColumn: Grid.Column<Client>? = null
    private var amountColumn: Grid.Column<Client>? = null
    private var statusColumn: Grid.Column<Client>? = null
    private var dateColumn: Grid.Column<Client>? = null

    private val clients: List<Client>
        get() = Arrays.asList(
                createClient(4957,
                        "https://randomuser.me/api/portraits/women/42.jpg",
                        "Amarachi Nkechi", 47427.0, "Success", "2019-05-09"),
                createClient(675,
                        "https://randomuser.me/api/portraits/women/24.jpg",
                        "Bonelwa Ngqawana", 70503.0, "Success", "2019-05-09"),
                createClient(6816,
                        "https://randomuser.me/api/portraits/men/42.jpg",
                        "Debashis Bhuiyan", 58931.0, "Success", "2019-05-07"),
                createClient(5144,
                        "https://randomuser.me/api/portraits/women/76.jpg",
                        "Jacqueline Asong", 25053.0, "Pending", "2019-04-25"),
                createClient(9800,
                        "https://randomuser.me/api/portraits/men/24.jpg",
                        "Kobus van de Vegte", 7319.0, "Pending", "2019-04-22"),
                createClient(3599,
                        "https://randomuser.me/api/portraits/women/94.jpg",
                        "Mattie Blooman", 18441.0, "Error", "2019-04-17"),
                createClient(3989,
                        "https://randomuser.me/api/portraits/men/76.jpg",
                        "Oea Romana", 33376.0, "Pending", "2019-04-17"),
                createClient(1077,
                        "https://randomuser.me/api/portraits/men/94.jpg",
                        "Stephanus Huggins", 75774.0, "Success", "2019-02-26"),
                createClient(8942,
                        "https://randomuser.me/api/portraits/men/16.jpg",
                        "Torsten Paulsson", 82531.0, "Pending", "2019-02-21"))

    init {
        setId("card-list-view")
        setSizeFull()
        createGrid()
        add(grid!!)
    }

    private fun createGrid() {
        createGridComponent()
        addColumnsToGrid()
        addFiltersToGrid()
    }

    private fun createGridComponent() {
        grid = GridPro()
        grid!!.setSelectionMode(SelectionMode.MULTI)
        grid!!.addThemeVariants(GridVariant.LUMO_NO_BORDER,
                GridVariant.LUMO_COLUMN_BORDERS)
        grid!!.height = "100%"

        dataProvider = ListDataProvider(clients)
        grid!!.dataProvider = dataProvider!!
    }

    private fun addColumnsToGrid() {
        createIdColumn()
        createClientColumn()
        createAmountColumn()
        createStatusColumn()
        createDateColumn()
    }

    private fun createIdColumn() {
        idColumn = grid!!.addColumn<Int>(ValueProvider<Client, Int> { it.id }, "id").setHeader("ID")
                .setWidth("120px").setFlexGrow(0)
    }

    private fun createClientColumn() {
        clientColumn = grid!!.addColumn(ComponentRenderer<HorizontalLayout, Client> { client ->
            val hl = HorizontalLayout()
            hl.alignItems = Alignment.CENTER
            val img = Image(client.img, "")
            val span = Span()
            span.className = "name"
            span.text = client.client
            hl.add(img, span)
            hl
        }).setComparator<String> { client -> client.client }.setHeader("Client")
    }

    private fun createAmountColumn() {
        amountColumn = grid!!
                .addEditColumn(ValueProvider<Client, Any> { it.amount },
                        NumberRenderer({ client -> client.amount },
                                NumberFormat.getCurrencyInstance(Locale.US)))
                .text { item, newValue ->
                    item
                            .amount = java.lang.Double.parseDouble(newValue)
                }
                .setComparator { client -> client.amount }
                .setHeader("Amount")
    }

    private fun createStatusColumn() {
        statusColumn = grid!!.addEditColumn(ValueProvider<Client, Any> { it.client },
                ComponentRenderer<Span, Client> { client ->
                    val span = Span()
                    span.text = client.status
                    span.element.setAttribute("theme",
                            "badge " + client.status.toString())
                    span
                })
                .select({ item, newValue -> item.status = newValue },
                        Arrays.asList("Pending", "Success", "Error"))
                .setComparator<String> { client -> client.status!! }
                .setHeader("Status")
    }

    private fun createDateColumn() {
        dateColumn = grid!!
                .addColumn(LocalDateRenderer(
                        { client -> LocalDate.parse(client.date!!) },
                        DateTimeFormatter.ofPattern("M/d/yyyy")))
                .setComparator<String> { client -> client.date }.setHeader("Date")
                .setWidth("180px").setFlexGrow(0)
    }

    private fun addFiltersToGrid() {
        val filterRow = grid!!.appendHeaderRow()

        val idFilter = TextField()
        idFilter.placeholder = "Filter"
        idFilter.isClearButtonVisible = true
        idFilter.width = "100%"
        idFilter.valueChangeMode = ValueChangeMode.EAGER
        idFilter.addValueChangeListener { event ->
            dataProvider!!.addFilter { client ->
                StringUtils
                        .containsIgnoreCase(Integer.toString(client.id),
                                idFilter.value)
            }
        }
        filterRow.getCell(idColumn!!).setComponent(idFilter)

        val clientFilter = TextField()
        clientFilter.placeholder = "Filter"
        clientFilter.isClearButtonVisible = true
        clientFilter.width = "100%"
        clientFilter.valueChangeMode = ValueChangeMode.EAGER
        clientFilter.addValueChangeListener { event ->
            dataProvider!!.addFilter { client ->
                StringUtils.containsIgnoreCase(client.client,
                        clientFilter.value)
            }
        }
        filterRow.getCell(clientColumn!!).setComponent(clientFilter)

        val amountFilter = TextField()
        amountFilter.placeholder = "Filter"
        amountFilter.isClearButtonVisible = true
        amountFilter.width = "100%"
        amountFilter.valueChangeMode = ValueChangeMode.EAGER
        amountFilter.addValueChangeListener { event ->
            dataProvider!!.addFilter { client ->
                StringUtils
                        .containsIgnoreCase(java.lang.Double.toString(client.amount),
                                amountFilter.value)
            }
        }
        filterRow.getCell(amountColumn!!).setComponent(amountFilter)

        val statusFilter = ComboBox<String>()
        statusFilter.setItems(Arrays.asList("Pending", "Success", "Error"))
        statusFilter.placeholder = "Filter"
        statusFilter.isClearButtonVisible = true
        statusFilter.width = "100%"
        statusFilter.addValueChangeListener { event ->
            dataProvider!!.addFilter { client -> areStatusesEqual(client, statusFilter) }
        }
        filterRow.getCell(statusColumn!!).setComponent(statusFilter)

        val dateFilter = DatePicker()
        dateFilter.placeholder = "Filter"
        dateFilter.isClearButtonVisible = true
        dateFilter.width = "100%"
        dateFilter.addValueChangeListener { event ->
            dataProvider!!
                    .addFilter { client -> areDatesEqual(client, dateFilter) }
        }
        filterRow.getCell(dateColumn!!).setComponent(dateFilter)
    }

    private fun areStatusesEqual(client: Client,
                                 statusFilter: ComboBox<String>): Boolean {
        val statusFilterValue = statusFilter.value
        return if (statusFilterValue != null) {
            StringUtils.equals(client.status, statusFilterValue)
        } else true
    }

    private fun areDatesEqual(client: Client, dateFilter: DatePicker): Boolean {
        val dateFilterValue = dateFilter.value
        if (dateFilterValue != null) {
            val clientDate = LocalDate.parse(client.date!!)
            return dateFilterValue == clientDate
        }
        return true
    }

    private fun createClient(id: Int, img: String, client: String,
                             amount: Double, status: String, date: String): Client {
        val c = Client()
        c.id = id
        c.img = img
        c.client = client
        c.amount = amount
        c.status = status
        c.date = date

        return c
    }
}

