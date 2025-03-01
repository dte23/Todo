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
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.navigationBarsPadding

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
    val icon: ImageVector,
    val myCheckListElements: MutableList<MyCheckListElement>
)

class MyCheckListElement(
    val text: String,
    checked: Boolean
) {
    var checked by mutableStateOf(checked)
}

@Composable
fun ChecklistApp() {
    var isTwoColumnView by remember { mutableStateOf(false) }
    var checklists by remember { mutableStateOf(DataSource().loadDemoCheckLists()) }
    var listCounter by remember { mutableIntStateOf(checklists.size) }
    var checkedCounter by remember {
        mutableIntStateOf(checklists.sumOf { it.myCheckListElements.count { item -> item.checked } })
    }
    // Store expanded state for each checklist by index.
    val expandedStates = remember { mutableStateMapOf<Int, Boolean>() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Global Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.House,
                contentDescription = "Header icon"
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "MineHuskelister",
                fontSize = 22.sp,
                textAlign = TextAlign.Center
            )
        }
        // "Vis som to kolonner" row
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
        // Divider Header
        HorizontalDivider(
            thickness = 2.dp,
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.outline
        )
        // Placeholder button row (no extra space added)
        Button(
            onClick = { /* Placeholder action */ },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .fillMaxWidth()
                .padding(4.dp)
        ) {
            Text(text = "Placeholder button")
        }
        // "Fullførte oppgaver" row
        Text(
            text = "$checkedCounter av ${checklists.sumOf { it.myCheckListElements.size }} er utført",
            modifier = Modifier.align(Alignment.CenterHorizontally),
            fontSize = 18.sp
        )
        // Grid med lister av oppgaver
        Box(modifier = Modifier.weight(1f)) {
            if (isTwoColumnView) {
                LazyVerticalGrid(columns = GridCells.Fixed(2), modifier = Modifier.fillMaxSize()) {
                    items(checklists.size) { index ->
                        val isExpanded = expandedStates.getOrPut(index) { true }
                        ChecklistView(
                            checklist = checklists[index],
                            onCheckedChange = {
                                checkedCounter = checklists.sumOf { it.myCheckListElements.count { item -> item.checked } }
                            },
                            onDeleteList = {
                                checklists = checklists.toMutableList().apply { removeAt(index) }
                                listCounter = checklists.size
                                checkedCounter = checklists.sumOf { it.myCheckListElements.count { item -> item.checked } }
                            },
                            onCompleteAll = { /* No extra action needed here */ },
                            onEditList = { /* Placeholder */ },
                            isExpanded = isExpanded,
                            onToggleExpanded = { expandedStates[index] = !expandedStates[index]!! }
                        )
                    }
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(checklists.size) { index ->
                        val isExpanded = expandedStates.getOrPut(index) { true }
                        ChecklistView(
                            checklist = checklists[index],
                            onCheckedChange = {
                                checkedCounter = checklists.sumOf { it.myCheckListElements.count { item -> item.checked } }
                            },
                            onDeleteList = {
                                checklists = checklists.toMutableList().apply { removeAt(index) }
                                listCounter = checklists.size
                                checkedCounter = checklists.sumOf { it.myCheckListElements.count { item -> item.checked } }
                            },
                            onCompleteAll = { /* No extra action needed here */ },
                            onEditList = { /* Placeholder */ },
                            isExpanded = isExpanded,
                            onToggleExpanded = { expandedStates[index] = !expandedStates[index]!! }
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier
            .height(4.dp)
        )
        HorizontalDivider(
            thickness = 2.dp,
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.outline
        )
        // Lister text at the bottom, with navigation bar padding.
        Text(
            text = "Totalt $listCounter lister.",
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
                .navigationBarsPadding(),
            textAlign = TextAlign.Center,
            fontSize = 18.sp
        )
    }
}






@Composable
fun ChecklistView(
    checklist: MyCheckList,
    onCheckedChange: () -> Unit,
    onDeleteList: () -> Unit,
    onCompleteAll: () -> Unit,
    onEditList: () -> Unit,
    isExpanded: Boolean,
    onToggleExpanded: () -> Unit
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
        // Use a Column with SpaceBetween to pin header at top and footer at bottom.
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // HEADER: Displays the checklist icon, name, and the toggle button.
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = checklist.icon,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = checklist.name,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onToggleExpanded) {
                    if (isExpanded) {
                        Icon(
                            imageVector = Icons.Filled.KeyboardArrowUp,
                            contentDescription = "Collapse checklist"
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Filled.KeyboardArrowDown,
                            contentDescription = "Expand checklist"
                        )
                    }
                }
            }
            // TASK AREA: Only shown if expanded.
            if (isExpanded) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(
                            min = rowHeight * totalRows,
                            max = rowHeight * maxVisibleItems
                        )
                ) {
                    if (totalRows <= maxVisibleItems) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            checklist.myCheckListElements.forEach { element ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(rowHeight)
                                        .clickable {
                                            element.checked = !element.checked
                                            onCheckedChange()
                                        },
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Switch(
                                        checked = element.checked,
                                        onCheckedChange = {
                                            element.checked = it
                                            onCheckedChange()
                                        },
                                        modifier = Modifier.scale(0.6f)
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
                                .height(rowHeight * maxVisibleItems),
                            verticalArrangement = Arrangement.spacedBy(0.dp)
                        ) {
                            items(checklist.myCheckListElements.size) { index ->
                                val element = checklist.myCheckListElements[index]
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(rowHeight)
                                        .clickable {
                                            element.checked = !element.checked
                                            onCheckedChange()
                                        },
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Switch(
                                        checked = element.checked,
                                        onCheckedChange = {
                                            element.checked = it
                                            onCheckedChange()
                                        },
                                        modifier = Modifier.scale(0.6f)
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
            // FOOTER: Contains three buttons arranged evenly.
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    checklist.myCheckListElements.forEach { it.checked = true }
                    onCheckedChange()
                    onCompleteAll()
                }) {
                    Icon(
                        imageVector = Icons.Filled.DoneAll,
                        contentDescription = "Complete all tasks"
                    )
                }
                IconButton(onClick = onDeleteList) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = "Delete checklist"
                    )
                }
                IconButton(onClick = onEditList) {
                    Icon(
                        imageVector = Icons.Filled.Edit,
                        contentDescription = "Edit checklist"
                    )
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
                    MyCheckListElement("Kjøp melk", false)
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
