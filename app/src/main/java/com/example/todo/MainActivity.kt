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
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Dining
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.House
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp

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

class MyCheckListElement(
    val text: String,
    checked: Boolean,
) {
    var checked by mutableStateOf(checked)
}

// example list fra Tips Oblig2



@Composable
fun ChecklistApp() {
    var isTwoColumnView by remember { mutableStateOf(false) }
    var checklists by remember { mutableStateOf(DataSource().loadDemoCheckLists()) }
    var listCounter by remember { mutableIntStateOf(checklists.size) }
    var checkedCounter by remember {
        mutableIntStateOf(checklists.sumOf { it.myCheckListElements.count { item -> item.checked } })
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.House,
                contentDescription = "Header icon",
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "MineHuskelister",
                fontSize = 22.sp,
                textAlign = TextAlign.Center
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Vis som to kolonner",
                modifier = Modifier.padding(8.dp),
                fontSize = 16.sp
            )
            Switch(
                modifier = Modifier.scale(0.6f),
                checked = isTwoColumnView,
                onCheckedChange = { isTwoColumnView = it }
            )
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
    val rowHeight = 48.dp
    val totalRows = checklist.myCheckListElements.size
    val maxVisibleItems = 4

    Card(
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(6.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Header row remains the same
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 0.dp)
            ) {
                Icon(
                    imageVector = checklist.icon,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = checklist.name,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )
                // Complete all, Delete, Edit Buttons
                IconButton(onClick = onDeleteList) {
                    Icon(
                        imageVector = Icons.Filled.DoneAll,
                        contentDescription = "Complete all tasks"
                    )
                }
                IconButton(onClick = onDeleteList) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = "Slett liste"
                    )
                }
                IconButton(onClick = onDeleteList) {
                    Icon(
                        imageVector = Icons.Filled.Edit,
                        contentDescription = "Edit list"
                    )
                }
            }
            // Use a Column for few items, and a LazyColumn when there are more than maxVisibleItems
            if (totalRows <= maxVisibleItems) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    checklist.myCheckListElements.forEach { element ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(rowHeight)
                                .clickable {
                                    element.checked = !element.checked
                                    onCheckedChange()
                                }
                        ) {
                            Switch(
                                modifier = Modifier.scale(0.6f),
                                checked = element.checked,
                                onCheckedChange = {
                                    element.checked = it
                                    onCheckedChange()
                                }
                            )
                            Text(
                                text = element.text,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        // Fix the height to show exactly maxVisibleItems rows
                        .height(rowHeight * maxVisibleItems),
                    verticalArrangement = Arrangement.spacedBy(0.dp)
                ) {
                    items(checklist.myCheckListElements.size) { index ->
                        val element = checklist.myCheckListElements[index]
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(rowHeight)
                                .clickable {
                                    element.checked = !element.checked
                                    onCheckedChange()
                                }
                        ) {
                            Switch(
                                modifier = Modifier.scale(0.6f),
                                checked = element.checked,
                                onCheckedChange = {
                                    element.checked = it
                                    onCheckedChange()
                                }
                            )
                            Text(
                                text = element.text,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }
    }
}



class DataSource {
    fun loadDemoCheckLists(): List<MyCheckList> {
        return listOf(
            MyCheckList(
                name = "Min todo-liste",
                icon = Icons.Filled.Face,
                myCheckListElements = mutableListOf(
                    MyCheckListElement("Kjøp melk", false),
//                    MyCheckListElement("Kjøp brød", true),
//                    MyCheckListElement("Kjøp smør", false),
//                    MyCheckListElement("Kjøp ost", true),
//                    MyCheckListElement("Kjøp skinke", false),
//                    MyCheckListElement("Kjøp syltetøy", true),
//                    MyCheckListElement("Kjøp knekkebrød", false),
//                    MyCheckListElement("Kjøp kaviar", true)
                )
            ),
            MyCheckList(
                name = "Husvask",
                icon = Icons.Filled.CleaningServices,
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
                icon = Icons.Filled.School,
                myCheckListElements = mutableListOf(
                    MyCheckListElement("Husk å vaske kjøkkenet", false),
                    MyCheckListElement("Husk å vaske badet", true),
                    MyCheckListElement("Husk å vaske stua", false),
                    MyCheckListElement("Husk å vaske soverommet", true)
                )
            ),
            MyCheckList(
                name = "Middagsplan",
                icon = Icons.Filled.Dining,
                myCheckListElements = mutableListOf(
                    MyCheckListElement("Gjør matteoppgaver", true),
                    MyCheckListElement("Gjør fysikkoppgaver", true),
                    MyCheckListElement("Gjør kjemioppgaver", true),
                    MyCheckListElement("Gjør biooppgaver", true)
                )
            ),
            MyCheckList(
                name = "Handleliste",
                icon = Icons.Filled.ShoppingCart,
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