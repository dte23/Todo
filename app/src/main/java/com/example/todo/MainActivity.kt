package com.example.todo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.ui.graphics.vector.ImageVector

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ChecklistApp()
        }
    }
}

data class MyCheckList(
    val name: String,
    val icon: ImageVector,  // Use ImageVector for icons
    val myCheckListElements: MutableList<MyCheckListElement>  // Use MutableList for state updates
)

data class MyCheckListElement(
    val text: String,
    var checked: Boolean,
)

class DataSource {
    fun loadDemoCheckLists(): List<MyCheckList> {
        return listOf(
            MyCheckList(
                name = "Min todo-liste",
                icon = Icons.Filled.Face,
                myCheckListElements = mutableListOf(
                    MyCheckListElement("Kjøp melk", false),
                    MyCheckListElement("Kjøp brød", true),
                    MyCheckListElement("Kjøp smør", false),
                    MyCheckListElement("Kjøp ost", true),
                    MyCheckListElement("Kjøp skinke", false),
                    MyCheckListElement("Kjøp syltetøy", true),
                    MyCheckListElement("Kjøp knekkebrød", false),
                    MyCheckListElement("Kjøp kaviar", true)
                )
            ),
            MyCheckList(
                name = "Husvask",
                icon = Icons.Filled.ShoppingCart,
                myCheckListElements = mutableListOf(
                    MyCheckListElement("Skriv søknad", false),
                    MyCheckListElement("Send søknad", true),
                    MyCheckListElement("Få jobb", false),
                    MyCheckListElement("Jobb hardt", true),
                    MyCheckListElement("Få lønn", false),
                    MyCheckListElement("Kjøp hus", true)
                )
            ),
            MyCheckList(
                name = "Studieplan",
                icon = Icons.Filled.LocationOn,
                myCheckListElements = mutableListOf(
                    MyCheckListElement("Husk å vaske kjøkkenet", false),
                    MyCheckListElement("Husk å vaske badet", true),
                    MyCheckListElement("Husk å vaske stua", false),
                    MyCheckListElement("Husk å vaske soverommet", true)
                )
            ),
            MyCheckList(
                name = "Middagsplan",
                icon = Icons.Filled.Home,
                myCheckListElements = mutableListOf(
                    MyCheckListElement("Gjør matteoppgaver", true),
                    MyCheckListElement("Gjør fysikkoppgaver", true),
                    MyCheckListElement("Gjør kjemioppgaver", true),
                    MyCheckListElement("Gjør biooppgaver", true)
                )
            ),
            MyCheckList(
                name = "Handleliste",
                icon = Icons.Filled.AccountCircle,
                myCheckListElements = mutableListOf(
                    MyCheckListElement("Lag middag", true),
                    MyCheckListElement("Spis middag", true),
                    MyCheckListElement("Vask opp", true),
                    MyCheckListElement("Gå tur", true),
                    MyCheckListElement("Se på TV", true)
                )
            )
        )
    }
}

@Composable
fun ChecklistApp() {
    var isTwoColumnView by remember { mutableStateOf(false) }
    var checklists by remember { mutableStateOf(DataSource().loadDemoCheckLists()) }

    var listCounter by remember { mutableIntStateOf(checklists.size) }
    var checkedCounter by remember {
        mutableIntStateOf(checklists.sumOf { it.myCheckListElements.count { item -> item.checked } })
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("To kolonner")
            Switch(checked = isTwoColumnView, onCheckedChange = { isTwoColumnView = it })
        }

        Text("Lister: $listCounter | Fullførte oppgaver: $checkedCounter")

        if (isTwoColumnView) {
            LazyVerticalGrid(columns = GridCells.Fixed(2), modifier = Modifier.fillMaxSize()) {
                items(checklists.size) { index ->
                    ChecklistView(
                        checklist = checklists[index],
                        onCheckedChange = {
                            checkedCounter = checklists.sumOf { it.myCheckListElements.count { item -> item.checked } }
                        },
                        onDeleteList = {
                            checklists = checklists.toMutableList().apply { removeAt(index) }
                            listCounter = checklists.size
                            checkedCounter = checklists.sumOf { it.myCheckListElements.count { item -> item.checked } }
                        }
                    )
                }
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(checklists.size) { index ->
                    ChecklistView(
                        checklist = checklists[index],
                        onCheckedChange = {
                            checkedCounter = checklists.sumOf { it.myCheckListElements.count { item -> item.checked } }
                        },
                        onDeleteList = {
                            checklists = checklists.toMutableList().apply { removeAt(index) }
                            listCounter = checklists.size
                            checkedCounter = checklists.sumOf { it.myCheckListElements.count { item -> item.checked } }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ChecklistView(
    checklist: MyCheckList,
    onCheckedChange: () -> Unit,
    onDeleteList: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp) // Fixed height for scrolling
            .padding(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Icon(
                    imageVector = checklist.icon, // Use ImageVector for Material Icons
                    contentDescription = null,
                    modifier = Modifier.size(24.dp).padding(end = 8.dp)
                )
                Text(text = checklist.name, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))

                IconButton(onClick = onDeleteList) {
                    Icon(imageVector = Icons.Filled.Delete, contentDescription = "Slett liste")
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            LazyColumn {
                items(checklist.myCheckListElements.size) { index ->
                    val element = checklist.myCheckListElements[index]
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                // Toggle checked state properly
                                element.checked = !element.checked
                                onCheckedChange()
                            }
                            .padding(vertical = 4.dp)
                    ) {
                        Checkbox(checked = element.checked, onCheckedChange = {
                            element.checked = it
                            onCheckedChange()
                        })
                        Text(text = element.text, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}
