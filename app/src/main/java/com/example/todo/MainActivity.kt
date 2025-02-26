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
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.House
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

val availableIcons = listOf(
    Pair(Icons.Filled.ShoppingCart, "Shopping"),
    Pair(Icons.Filled.Home, "Home"),
    Pair(Icons.Filled.Face, "Person"),
    Pair(Icons.Filled.Favorite, "Favorite"),
    Pair(Icons.Filled.Star, "Star"),
    Pair(Icons.Filled.Settings, "Settings"),
    Pair(Icons.Filled.List, "List"),
    Pair(Icons.Filled.Warning, "Warning"),
    Pair(Icons.Filled.Info, "Info"),
    Pair(Icons.Filled.Build, "Tools"),
    Pair(Icons.Filled.Create, "Create"),
    Pair(Icons.Filled.Notifications, "Notifications")
)

data class MyCheckList(
    val name: String,
    val icon: ImageVector,
    val myCheckListElements: MutableList<MyCheckListElement>
)

data class MyCheckListElement(
    val text: String,
    var checked: Boolean
)

class DataSource {
    fun loadDemoCheckLists(): List<MyCheckList> {
        return listOf(
            MyCheckList(
                name = "Handleliste",
                icon = Icons.Filled.ShoppingCart,
                myCheckListElements = mutableListOf(
                    MyCheckListElement("Melk", false),
                    MyCheckListElement("Brød", false),
                    MyCheckListElement("Egg", false)
                )
            ),
            MyCheckList(
                name = "Husarbeid",
                icon = Icons.Filled.Home,
                myCheckListElements = mutableListOf(
                    MyCheckListElement("Støvsuge", false),
                    MyCheckListElement("Vaske badet", false),
                    MyCheckListElement("Skifte sengetøy", false)
                )
            )
        )
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ChecklistApp()
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
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
//                modifier = Modifier.fillMaxWidth(),
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
                modifier = Modifier.scale(0.7f),
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
        
        Text(
            text = "Totalt ${checklists.size} lister.",
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            fontSize = 14.sp,
            color = Color.DarkGray
        )
    }

    // Add New List Dialog
    if (showAddListDialog) {
        ListDialog(
            title = "Legg til ny sjekkliste",
            initialName = "",
            initialIcon = Icons.Filled.Face,
            initialItems = listOf(MyCheckListElement("", false)),
            onDismiss = { showAddListDialog = false },
            onConfirm = { name, icon, items ->
                if (name.isNotBlank() && items.any { it.text.isNotBlank() }) {
                    val newList = MyCheckList(
                        name = name,
                        icon = icon,
                        myCheckListElements = items.filter { it.text.isNotBlank() }.toMutableList()
                    )
                    checklists = checklists.toMutableList().apply { add(newList) }
                }
                showAddListDialog = false
            }
        )
    }

    // Edit List Dialog
    if (showEditListDialog && currentEditListIndex >= 0 && currentEditListIndex < checklists.size) {
        val listToEdit = checklists[currentEditListIndex]
        ListDialog(
            title = "Rediger sjekkliste",
            initialName = listToEdit.name,
            initialIcon = listToEdit.icon,
            initialItems = listToEdit.myCheckListElements.toList(),
            onDismiss = { showEditListDialog = false },
            onConfirm = { name, icon, items ->
                if (name.isNotBlank() && items.any { it.text.isNotBlank() }) {
                    val updatedList = MyCheckList(
                        name = name,
                        icon = icon,
                        myCheckListElements = items.filter { it.text.isNotBlank() }.toMutableList()
                    )
                    checklists = checklists.toMutableList().apply { set(currentEditListIndex, updatedList) }
                }
                showEditListDialog = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun ListDialog(
    title: String,
    initialName: String,
    initialIcon: ImageVector,
    initialItems: List<MyCheckListElement>,
    onDismiss: () -> Unit,
    onConfirm: (name: String, icon: ImageVector, items: List<MyCheckListElement>) -> Unit
) {
    var listName by remember { mutableStateOf(initialName) }
    var selectedIcon by remember { mutableStateOf(initialIcon) }
    
    // Ensure there's always an empty item at the end for adding new items
    val itemsWithEmptyLast = if (initialItems.isEmpty() || initialItems.last().text.isNotBlank()) {
        initialItems + MyCheckListElement("", false)
    } else {
        initialItems
    }
    
    var items by remember { mutableStateOf(itemsWithEmptyLast) }
    var showIconSelector by remember { mutableStateOf(false) }
    
    val keyboardController = LocalSoftwareKeyboardKController.current
    val focusRequester = remember { FocusRequester() }
    
    LaunchedEffect(Unit) {
        delay(100)
        focusRequester.requestFocus()
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                // Icon selection button
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                        .clickable { showIconSelector = !showIconSelector }
                ) {
                    Icon(
                        imageVector = selectedIcon,
                        contentDescription = null,
                        modifier = Modifier.size(28.dp)
                    )
                    Text(
                        text = "Velg ikon",
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
                
                // Icon selector
                if (showIconSelector) {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(4),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                    ) {
                        items(availableIcons.size) { index ->
                            val (icon, description) = availableIcons[index]
                            IconButton(
                                onClick = {
                                    selectedIcon = icon
                                    showIconSelector = false
                                },
                                modifier = Modifier.padding(4.dp)
                            ) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = description,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }
                }
                
                // List name input
                TextField(
                    value = listName,
                    onValueChange = { listName = it },
                    label = { Text("Navn på sjekkliste") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                        .focusRequester(focusRequester),
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences,
                        imeAction = ImeAction.Next
                    )
                )
                
                // List items
                Text(
                    text = "Elementer",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                ) {
                    items(items.size) { index ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            TextField(
                                value = items[index].text,
                                onValueChange = { newText ->
                                    val updatedItems = items.toMutableList()
                                    updatedItems[index] = MyCheckListElement(newText, items[index].checked)
                                    
                                    // If this is the last item and it now has text, add a new empty item
                                    if (index == items.size - 1 && newText.isNotBlank()) {
                                        updatedItems.add(MyCheckListElement("", false))
                                    }
                                    
                                    items = updatedItems
                                },
                                placeholder = { Text("Skriv elementtekst her") },
                                singleLine = true,
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(end = 8.dp),
                                keyboardOptions = KeyboardOptions(
                                    capitalization = KeyboardCapitalization.Sentences,
                                    imeAction = ImeAction.Next
                                )
                            )
                            
                            if (items.size > 1 && items[index].text.isNotBlank()) {
                                IconButton(onClick = {
                                    val updatedItems = items.toMutableList().apply { 
                                        removeAt(index) 
                                    }
                                    
                                    // Ensure there's still an empty item at the end after removal
                                    if (updatedItems.isEmpty() || updatedItems.last().text.isNotBlank()) {
                                        updatedItems.add(MyCheckListElement("", false))
                                    }
                                    
                                    items = updatedItems
                                }) {
                                    Icon(
                                        imageVector = Icons.Filled.Close,
                                        contentDescription = "Fjern element",
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }
                
                // Action buttons
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    TextButton(
                        onClick = onDismiss
                    ) {
                        Text("Avbryt")
                    }
                    
                    Button(
                        onClick = {
                            keyboardController?.hide()
                            // Filter out empty items before saving
                            val nonEmptyItems = items.filter { it.text.isNotBlank() }
                            onConfirm(listName, selectedIcon, nonEmptyItems)
                        },
                        enabled = listName.isNotBlank() && items.any { it.text.isNotBlank() }
                    ) {
                        Text("Lagre")
                    }
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
            .padding(6.dp)
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
            Spacer(modifier = Modifier.height(1.dp))
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
//                            .padding(vertical = 2.dp)
                    ) {
                        Switch(
                            modifier = Modifier.scale(0.7f),
                            checked = element.checked,
                            onCheckedChange = {
                                element.checked = it
                                onCheckedChange()
                            }
                        )

                        Text(text = element.text, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}

