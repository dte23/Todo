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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Adb
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Dining
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.House
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.foundation.layout.navigationBarsPadding
import kotlin.random.Random

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
fun ChecklistsDisplay(
    checklists: List<MyCheckList>,
    isTwoColumnView: Boolean,
    expandedStates: MutableMap<Int, Boolean>,
    onCheckedChange: (Int) -> Unit,
    onDeleteList: (Int) -> Unit,
    onToggleExpanded: (Int) -> Unit
) {
    if (isTwoColumnView) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize()
        ) {
            items(checklists.size) { index ->
                val isExpanded = expandedStates.getOrPut(index) { true }
                ChecklistView(
                    checklist = checklists[index],
                    onCheckedChange = { onCheckedChange(index) },
                    onDeleteList = { onDeleteList(index) },
                    isExpanded = isExpanded,
                    onToggleExpanded = { onToggleExpanded(index) }
                )
            }
        }
    } else {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(checklists.size) { index ->
                val isExpanded = expandedStates.getOrPut(index) { true }
                ChecklistView(
                    checklist = checklists[index],
                    onCheckedChange = { onCheckedChange(index) },
                    onDeleteList = { onDeleteList(index) },
                    isExpanded = isExpanded,
                    onToggleExpanded = { onToggleExpanded(index) }
                )
            }
        }
    }
}

@Composable
fun AppHeader() {
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
}

@Composable
fun ToggleRow(isTwoColumnView: Boolean, onToggle: (Boolean) -> Unit) {
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
            checked = isTwoColumnView,
            onCheckedChange = onToggle,
            modifier = Modifier.scale(0.6f)
        )
    }
}

@Composable
fun ChecklistApp() {
    var isTwoColumnView by remember { mutableStateOf(false) }
    var checklists by remember { mutableStateOf(DataSource().loadDemoCheckLists()) }
    var listCounter by remember { mutableIntStateOf(checklists.size) }
    var checkedCounter by remember {
        mutableIntStateOf(
            checklists.sumOf { cl -> cl.myCheckListElements.count { item -> item.checked } }
        )
    }
    val expandedStates = remember { mutableStateMapOf<Int, Boolean>() }
    var counter by remember { mutableIntStateOf(1) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        AppHeader()
        ToggleRow(isTwoColumnView = isTwoColumnView, onToggle = { isTwoColumnView = it })
        HorizontalDivider(
            thickness = 2.dp,
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.outline
        )
        Button(
            onClick = {
                val checklist = newChecklist()
                val updatedChecklist = checklist.copy(name = "Random sjekkliste $counter")
                counter++
                checklists = checklists.toMutableList().apply { add(updatedChecklist) }
                listCounter = checklists.size
                checkedCounter = checklists.sumOf { cl -> cl.myCheckListElements.count { item -> item.checked } }
            },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .fillMaxWidth()
                .padding(4.dp)
        ) {
            Text(text = "Legg til ny sjekkliste")
        }
        Text(
            text = "$checkedCounter av ${checklists.sumOf { cl -> cl.myCheckListElements.size }} er utført",
            modifier = Modifier.align(Alignment.CenterHorizontally),
            fontSize = 18.sp
        )
        Box(modifier = Modifier.weight(1f)) {
            ChecklistsDisplay(
                checklists = checklists,
                isTwoColumnView = isTwoColumnView,
                expandedStates = expandedStates,
                onCheckedChange = { _ ->
                    checkedCounter = checklists.sumOf { cl -> cl.myCheckListElements.count { item -> item.checked } }
                },
                onDeleteList = { index ->
                    checklists = checklists.toMutableList().apply { removeAt(index) }
                    listCounter = checklists.size
                    checkedCounter = checklists.sumOf { cl -> cl.myCheckListElements.count { item -> item.checked } }
                },
                onToggleExpanded = { index ->
                    expandedStates[index] = !expandedStates[index]!!
                }
            )
        }
        HorizontalDivider(
            thickness = 2.dp,
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.outline
        )
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
fun DeleteConfirmationDialog(
    checklistName: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Delete list: $checklistName?") },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Icon(
                    imageVector = Icons.Filled.Done,
                    contentDescription = "Delete confirm"
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = "Delete dismiss"
                )
            }
        }
    )
}

@Composable
fun ChecklistHeader(
    checklist: MyCheckList,
    isExpanded: Boolean,
    onToggleExpanded: () -> Unit
) {
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
}

@Composable
fun ChecklistTaskArea(
    checklist: MyCheckList,
    onCheckedChange: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(
                min = 48.dp * checklist.myCheckListElements.size,
                max = 48.dp * 4
            )
    ) {
        if (checklist.myCheckListElements.size <= 4) {
            Column(modifier = Modifier.fillMaxWidth()) {
                checklist.myCheckListElements.forEach { element ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
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
                    .height(48.dp * 4),
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                items(checklist.myCheckListElements.size) { index ->
                    val element = checklist.myCheckListElements[index]
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
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

@Composable
fun ChecklistFooter(
    checklist: MyCheckList,
    onCheckedChange: () -> Unit,
    onDeleteList: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = {
            // Complete alle oppgaver i listen
            checklist.myCheckListElements.forEach { it.checked = true }
            onCheckedChange()
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
        // Edit ikonet men ikke lagt til funksjonalitet
        Icon(
            imageVector = Icons.Filled.Edit,
            contentDescription = "Edit checklist"
        )
    }
}

@Composable
fun ChecklistView(
    checklist: MyCheckList,
    onCheckedChange: () -> Unit,
    onDeleteList: () -> Unit,
    isExpanded: Boolean,
    onToggleExpanded: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        DeleteConfirmationDialog(
            checklistName = checklist.name,
            onConfirm = {
                onDeleteList()
                showDeleteDialog = false
            },
            onDismiss = { showDeleteDialog = false }
        )
    }

    Card(
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(6.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            ChecklistHeader(
                checklist = checklist,
                isExpanded = isExpanded,
                onToggleExpanded = onToggleExpanded
            )
            if (isExpanded) {
                ChecklistTaskArea(
                    checklist = checklist,
                    onCheckedChange = onCheckedChange
                )
            }
            ChecklistFooter(
                checklist = checklist,
                onCheckedChange = onCheckedChange,
                onDeleteList = { showDeleteDialog = true }
            )
        }
    }
}

fun newChecklist(): MyCheckList {
    val tasks = listOf(
        "Skriv søknad",
        "Send søknad",
        "Få jobb",
        "Jobb hardt",
        "Få lønn",
        "Kjøp hus",
        "Husk å vaske kjøkkenet",
        "Husk å vaske badet",
        "Husk å vaske stua",
        "Husk å vaske soverommet",
        "Gjør matteoppgaver",
        "Gjør fysikkoppgaver",
        "Gjør kjemioppgaver",
        "Gjør biooppgaver",
        "Lag middag",
        "Spis middag",
        "Vask opp",
        "Gå tur",
        "Se på TV"
    )

    val randomElements = tasks.shuffled().take(4)
    val checklistElements = randomElements.map {
        MyCheckListElement(it, Random.nextBoolean())
    }.toMutableList()

    return MyCheckList(
        name = "Liste fra Legg til ny sjekkliste",
        icon = Icons.Filled.Adb,
        myCheckListElements = checklistElements
    )
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
