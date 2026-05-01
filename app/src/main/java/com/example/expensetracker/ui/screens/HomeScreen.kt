package com.example.expensetracker.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.expensetracker.R
import com.example.expensetracker.ui.theme.*
import com.example.expensetracker.viewmodel.ExpenseViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HomeScreen(viewModel: ExpenseViewModel = viewModel()) {
    val totalIncome by viewModel.totalIncome.observeAsState(0.0)
    val totalExpense by viewModel.totalExpense.observeAsState(0.0)
    val transactions by viewModel.allTransactions.observeAsState(emptyList())
    val notifications by viewModel.allNotifications.observeAsState(emptyList())
    val userProfile by viewModel.userProfile.observeAsState()
    
    val currentMonthYear = SimpleDateFormat("MM-yyyy", Locale.getDefault()).format(Date())
    val budgets by viewModel.getBudgets(currentMonthYear).observeAsState(emptyList())
    
    var lastReadNotificationCount by remember { mutableIntStateOf(0) }
    val newNotificationsCount = (notifications.size - lastReadNotificationCount).coerceAtLeast(0)
    
    val balance = (totalIncome ?: 0.0) - (totalExpense ?: 0.0)
    var selectedTab by remember { mutableIntStateOf(0) }
    var currentScreen by remember { mutableStateOf("home") }
    var showAddDialog by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var isSearchVisible by remember { mutableStateOf(false) }
    var showNotificationPopup by remember { mutableStateOf(false) }
    var showClearDataDialog by remember { mutableStateOf(false) }

    if (showAddDialog) {
        AddTransactionDialog(
            selectedTab = selectedTab,
            onDismiss = { showAddDialog = false },
            onSave = { amount, type, category ->
                viewModel.addTransaction(amount, type, category)
                showAddDialog = false
            }
        )
    }

    if (showClearDataDialog) {
        AlertDialog(
            onDismissRequest = { showClearDataDialog = false },
            title = { Text(stringResource(R.string.clear_all_data), fontWeight = FontWeight.Bold) },
            text = { Text(stringResource(R.string.confirm_clear_data)) },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.clearAllData()
                        showClearDataDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryRed)
                ) {
                    Text(stringResource(R.string.clear), color = White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDataDialog = false }) {
                    Text(stringResource(R.string.cancel), color = PrimaryRed)
                }
            }
        )
    }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                currentScreen = currentScreen,
                onScreenSelected = { 
                    currentScreen = it
                    isSearchVisible = false 
                    showNotificationPopup = false
                    if (it == "notifications") {
                        lastReadNotificationCount = notifications.size
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BackgroundBeige)
                    .padding(innerPadding)
            ) {
                when (currentScreen) {
                    "home" -> HomeContent(
                        balance = balance,
                        selectedTab = selectedTab,
                        transactions = transactions,
                        budgets = budgets,
                        viewModel = viewModel,
                        notificationsCount = newNotificationsCount,
                        onTabSelected = { selectedTab = it },
                        onAddClick = { showAddDialog = true },
                        searchQuery = searchQuery,
                        onSearchQueryChange = { searchQuery = it },
                        isSearchVisible = isSearchVisible,
                        onToggleSearch = { isSearchVisible = !isSearchVisible },
                        onNotificationClick = { 
                            showNotificationPopup = !showNotificationPopup
                        }
                    )
                    "analytics" -> AnalyticsContent(transactions)
                    "records" -> RecordsContent(transactions)
                    "notifications" -> NotificationsContent(notifications)
                    "profile" -> ProfileContent(
                        userProfile = userProfile,
                        onUpdateProfile = { name, email -> viewModel.updateProfile(name, email) },
                        onClearDataClick = { showClearDataDialog = true },
                        onSetBudget = { category, amount -> viewModel.setBudget(category, amount, currentMonthYear) }
                    )
                }
            }

            AnimatedVisibility(
                visible = showNotificationPopup,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut(),
                modifier = Modifier
                    .padding(top = 80.dp, start = 24.dp, end = 24.dp)
                    .align(Alignment.TopStart)
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 400.dp)
                        .padding(bottom = 16.dp),
                    shape = RoundedCornerShape(24.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
                    colors = CardDefaults.cardColors(containerColor = White)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(stringResource(R.string.recent_activity), fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = DeepBlack)
                            TextButton(onClick = { 
                                currentScreen = "notifications"
                                showNotificationPopup = false
                            }) {
                                Text(stringResource(R.string.see_all), color = PrimaryRed, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        
                        androidx.compose.foundation.lazy.LazyColumn(
                            modifier = Modifier.weight(1f, fill = false)
                        ) {
                            if (notifications.isEmpty()) {
                                item {
                                    Text(stringResource(R.string.no_notifications), color = TextGray, modifier = Modifier.padding(vertical = 24.dp))
                                }
                            } else {
                                items(notifications.take(5).size) { index ->
                                    val notification = notifications[index]
                                    Column(modifier = Modifier.padding(vertical = 8.dp)) {
                                        Text(
                                            text = notification.message,
                                            fontSize = 14.sp,
                                            color = DeepBlack,
                                            lineHeight = 20.sp
                                        )
                                        if (index < notifications.take(5).size - 1) {
                                            HorizontalDivider(modifier = Modifier.padding(top = 8.dp), color = LightGray)
                                        }
                                    }
                                }
                            }
                        }
                        
                        Button(
                            onClick = { 
                                showNotificationPopup = false
                                lastReadNotificationCount = notifications.size
                            },
                            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryRed),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(stringResource(R.string.close), color = White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeContent(
    balance: Double,
    selectedTab: Int,
    transactions: List<com.example.expensetracker.data.model.Transaction>,
    budgets: List<com.example.expensetracker.data.model.Budget>,
    viewModel: ExpenseViewModel,
    notificationsCount: Int,
    onTabSelected: (Int) -> Unit,
    onAddClick: () -> Unit,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    isSearchVisible: Boolean,
    onToggleSearch: () -> Unit,
    onNotificationClick: () -> Unit
) {
    val filteredTransactions = remember(transactions, selectedTab, searchQuery) {
        transactions.filter { transaction ->
            val matchesTab = if (selectedTab == 0) transaction.type == "income" else transaction.type == "expense"
            val matchesSearch = transaction.amount.toString().contains(searchQuery, ignoreCase = true) ||
                    transaction.type.contains(searchQuery, ignoreCase = true) ||
                    transaction.category.contains(searchQuery, ignoreCase = true)
            matchesTab && matchesSearch
        }
    }

    Column {
        Header(
            isSearchVisible = isSearchVisible,
            searchQuery = searchQuery,
            onSearchQueryChange = onSearchQueryChange,
            onToggleSearch = onToggleSearch,
            onNotificationClick = onNotificationClick,
            notificationsCount = notificationsCount
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .height(52.dp)
                .clip(RoundedCornerShape(26.dp))
                .background(DeepBlack),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TabItem(
                title = stringResource(R.string.your_wallet),
                isSelected = selectedTab == 0,
                modifier = Modifier.weight(1f),
                onClick = { onTabSelected(0) }
            )
            TabItem(
                title = stringResource(R.string.expenses),
                isSelected = selectedTab == 1,
                modifier = Modifier.weight(1f),
                onClick = { onTabSelected(1) }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        BalanceCard(
            balance = balance,
            selectedTab = selectedTab,
            onAddClick = onAddClick
        )

        Spacer(modifier = Modifier.height(32.dp))
        
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(topStart = 48.dp, topEnd = 48.dp))
                .background(White)
                .padding(top = 32.dp, start = 24.dp, end = 24.dp)
        ) {
            androidx.compose.foundation.lazy.LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                if (selectedTab == 1 && budgets.isNotEmpty()) {
                    item {
                        Column {
                            Text(
                                text = "Budget Progress",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = DeepBlack
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                    items(budgets.size) { index ->
                        BudgetProgressItem(budgets[index], viewModel)
                    }
                    item { Spacer(modifier = Modifier.height(24.dp)) }
                }

                item {
                    Column {
                        Text(
                            text = if (selectedTab == 0) stringResource(R.string.income_history) else stringResource(R.string.expense_history),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = DeepBlack
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
                
                if (filteredTransactions.isEmpty()) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().height(150.dp), contentAlignment = Alignment.Center) {
                            Text(
                                text = if (selectedTab == 0) "No income records found" else "No expense records found",
                                color = TextGray,
                                fontSize = 14.sp
                            )
                        }
                    }
                } else {
                    items(filteredTransactions.size) { index ->
                        TransactionItem(filteredTransactions[index])
                    }
                }
            }
        }
    }
}

@Composable
fun AnalyticsContent(transactions: List<com.example.expensetracker.data.model.Transaction>) {
    val expenseTransactions = transactions.filter { it.type == "expense" }
    val totalExpense = expenseTransactions.sumOf { it.amount }
    val categoryTotals = expenseTransactions.groupBy { it.category }
        .mapValues { it.value.sumOf { it.amount } }
        .toList()
        .sortedByDescending { it.second }

    androidx.compose.foundation.lazy.LazyColumn(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            Column {
                Text(stringResource(R.string.analytics), fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = DeepBlack)
                Spacer(modifier = Modifier.height(24.dp))
                
                if (categoryTotals.isEmpty()) {
                    Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                        Text(stringResource(R.string.no_data_for_charts), color = TextGray)
                    }
                } else {
                    Text(stringResource(R.string.spending_summary), fontSize = 18.sp, fontWeight = FontWeight.Bold, color = DeepBlack)
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
        
        items(categoryTotals.size) { index ->
            val (category, amount) = categoryTotals[index]
            val percentage = (amount / totalExpense.coerceAtLeast(1.0)).toFloat()
            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = category, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = DeepBlack)
                        Text(
                            text = "₱${String.format("%.2f", amount)}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = PrimaryRed
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "${(percentage * 100).toInt()}% of total expenses", fontSize = 12.sp, color = TextGray)
                    Spacer(modifier = Modifier.height(12.dp))
                    LinearProgressIndicator(
                        progress = { percentage },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(CircleShape),
                        color = PrimaryRed,
                        trackColor = BackgroundBeige
                    )
                }
            }
        }
    }
}

@Composable
fun BudgetProgressItem(budget: com.example.expensetracker.data.model.Budget, viewModel: ExpenseViewModel) {
    val spending by viewModel.getCategorySpending(budget.category, budget.monthYear).observeAsState(0.0)
    val progress = ((spending ?: 0.0) / budget.limitAmount).toFloat().coerceIn(0f, 1f)
    
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = BackgroundBeige.copy(alpha = 0.2f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = budget.category, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = DeepBlack)
                Text(
                    text = "₱${String.format("%.0f", spending ?: 0.0)} / ₱${String.format("%.0f", budget.limitAmount)}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (progress > 0.9f) ErrorRed else PrimaryRed
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(CircleShape),
                color = if (progress > 0.9f) ErrorRed else PrimaryRed,
                trackColor = White
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Header(
    isSearchVisible: Boolean,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onToggleSearch: () -> Unit,
    onNotificationClick: () -> Unit,
    notificationsCount: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (isSearchVisible) {
            IconButton(onClick = onToggleSearch) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = DeepBlack)
            }
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                placeholder = { Text("Search transactions...", fontSize = 14.sp) },
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp),
                shape = RoundedCornerShape(26.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryRed,
                    unfocusedBorderColor = Taupe
                ),
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { onSearchQueryChange("") }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear", tint = TextGray)
                        }
                    }
                }
            )
        } else {
            IconButton(
                onClick = onNotificationClick,
                modifier = Modifier.size(48.dp).background(White, CircleShape)
            ) {
                BadgedBox(
                    badge = {
                        if (notificationsCount > 0) {
                            Badge(containerColor = PrimaryRed) {
                                Text(notificationsCount.toString(), color = White)
                            }
                        }
                    }
                ) {
                    Icon(Icons.Default.Notifications, contentDescription = "Notifications", tint = PrimaryRed, modifier = Modifier.size(26.dp))
                }
            }
            Text("CLOVERSAVE", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, color = DeepBlack, letterSpacing = 1.sp)
            IconButton(
                onClick = onToggleSearch,
                modifier = Modifier.size(48.dp).background(White, CircleShape)
            ) {
                Icon(Icons.Default.Search, contentDescription = "Search", tint = PrimaryRed, modifier = Modifier.size(26.dp))
            }
        }
    }
}

@Composable
fun RecordsContent(transactions: List<com.example.expensetracker.data.model.Transaction>) {
    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        Text(stringResource(R.string.transaction_records), fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = DeepBlack)
        Spacer(modifier = Modifier.height(24.dp))
        if (transactions.isEmpty()) {
            Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                Text(stringResource(R.string.no_records_found), color = TextGray)
            }
        } else {
            androidx.compose.foundation.lazy.LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(transactions.size) { index ->
                    TransactionItem(transactions[index])
                }
            }
        }
    }
}

@Composable
fun NotificationsContent(notifications: List<com.example.expensetracker.data.model.Notification>) {
    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        Text(stringResource(R.string.notifications), fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = DeepBlack)
        Spacer(modifier = Modifier.height(24.dp))
        if (notifications.isEmpty()) {
            Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                Text(stringResource(R.string.no_notifications), color = TextGray)
            }
        } else {
            androidx.compose.foundation.lazy.LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(notifications.size) { index ->
                    val notification = notifications[index]
                    val sdf = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault())
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Info, contentDescription = null, tint = PrimaryRed, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = sdf.format(Date(notification.timestamp)),
                                    fontSize = 12.sp,
                                    color = TextGray,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(notification.message, color = DeepBlack, fontSize = 14.sp, lineHeight = 20.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileContent(
    userProfile: com.example.expensetracker.data.model.UserProfile?,
    onUpdateProfile: (String, String) -> Unit,
    onClearDataClick: () -> Unit,
    onSetBudget: (String, Double) -> Unit
) {
    var name by remember { mutableStateOf(userProfile?.name ?: "") }
    var email by remember { mutableStateOf(userProfile?.email ?: "") }
    var showBudgetDialog by remember { mutableStateOf(false) }

    if (showBudgetDialog) {
        SetBudgetDialog(
            onDismiss = { showBudgetDialog = false },
            onSave = { category, amount ->
                onSetBudget(category, amount)
                showBudgetDialog = false
            }
        )
    }

    androidx.compose.foundation.lazy.LazyColumn(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.profile), fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = DeepBlack)
                Spacer(modifier = Modifier.height(32.dp))

                Box(
                    modifier = Modifier.size(100.dp).clip(CircleShape).background(White).align(Alignment.CenterHorizontally),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(60.dp), tint = PrimaryRed)
                }

                Spacer(modifier = Modifier.height(32.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(stringResource(R.string.name)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryRed, unfocusedBorderColor = Taupe)
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text(stringResource(R.string.email)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryRed, unfocusedBorderColor = Taupe)
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = { onUpdateProfile(name, email) },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryRed),
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(stringResource(R.string.update_profile), fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(32.dp))
                HorizontalDivider(color = LightGray)
                Spacer(modifier = Modifier.height(32.dp))

                Text("Budget Management", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = DeepBlack)
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { showBudgetDialog = true },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DeepBlack),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Set Monthly Budget", fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(24.dp))
                
                OutlinedButton(
                    onClick = { onClearDataClick() },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = PrimaryRed),
                    border = androidx.compose.foundation.BorderStroke(2.dp, PrimaryRed),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.clear_all_data), fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetBudgetDialog(onDismiss: () -> Unit, onSave: (String, Double) -> Unit) {
    var amount by remember { mutableStateOf("") }
    val categories = listOf(
        stringResource(R.string.cat_food),
        stringResource(R.string.cat_transport),
        stringResource(R.string.cat_bills),
        stringResource(R.string.cat_shopping),
        stringResource(R.string.cat_entertainment),
        stringResource(R.string.cat_health),
        stringResource(R.string.cat_other)
    )
    var selectedCategory by remember { mutableStateOf(categories[0]) }
    var expanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Set Monthly Budget", fontWeight = FontWeight.Bold) },
        text = {
            Column {
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = selectedCategory,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Select Category") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        categories.forEach { category ->
                            DropdownMenuItem(
                                text = { Text(category) },
                                onClick = {
                                    selectedCategory = category
                                    expanded = false
                                }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = amount,
                    onValueChange = { if (it.isEmpty() || it.toDoubleOrNull() != null) amount = it },
                    label = { Text("Monthly Limit") },
                    modifier = Modifier.fillMaxWidth(),
                    prefix = { Text("₱ ") },
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { amount.toDoubleOrNull()?.let { onSave(selectedCategory, it) } },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryRed),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Set Budget", color = White, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel), color = PrimaryRed, fontWeight = FontWeight.Bold)
            }
        }
    )
}

@Composable
fun TabItem(title: String, isSelected: Boolean, modifier: Modifier, onClick: () -> Unit) {
    Box(
        modifier = modifier
            .fillMaxHeight()
            .padding(4.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(if (isSelected) White else Color.Transparent)
            .padding(horizontal = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        TextButton(onClick = onClick, contentPadding = PaddingValues(0.dp)) {
            Text(
                text = title,
                color = if (isSelected) PrimaryRed else White,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun BottomNavigationBar(currentScreen: String, onScreenSelected: (String) -> Unit) {
    NavigationBar(
        containerColor = White,
        tonalElevation = 12.dp
    ) {
        val items = listOf(
            Triple("home", "Home", Icons.Default.Home),
            Triple("analytics", "Stats", Icons.Default.PieChart),
            Triple("records", "History", Icons.Default.History),
            Triple("notifications", "Notifs", Icons.Default.Notifications),
            Triple("profile", "Profile", Icons.Default.Person)
        )

        items.forEach { (id, title, icon) ->
            NavigationBarItem(
                icon = { Icon(imageVector = icon, contentDescription = title, tint = if (currentScreen == id) PrimaryRed else TextGray, modifier = Modifier.size(24.dp)) },
                label = { Text(text = title, fontSize = 10.sp, fontWeight = if (currentScreen == id) FontWeight.Bold else FontWeight.Normal) },
                selected = currentScreen == id,
                onClick = { onScreenSelected(id) },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = BackgroundBeige.copy(alpha = 0.5f),
                    selectedTextColor = PrimaryRed,
                    unselectedTextColor = TextGray
                )
            )
        }
    }
}

@Composable
fun BalanceCard(balance: Double, selectedTab: Int, onAddClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = PrimaryRed),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(28.dp)
        ) {
            Text(text = stringResource(R.string.total_balance), color = White.copy(alpha = 0.8f), fontSize = 14.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "₱ ${String.format("%.2f", balance)}",
                    color = White,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                IconButton(
                    onClick = onAddClick,
                    modifier = Modifier.size(48.dp).background(White.copy(alpha = 0.2f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add",
                        tint = White,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun TransactionItem(transaction: com.example.expensetracker.data.model.Transaction) {
    val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    val date = sdf.format(Date(transaction.timestamp))
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (transaction.type == "income") SuccessGreen.copy(alpha = 0.1f) else ErrorRed.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (transaction.type == "income") Icons.Default.TrendingUp else Icons.Default.TrendingDown,
                        contentDescription = null,
                        tint = if (transaction.type == "income") SuccessGreen else ErrorRed,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = transaction.category,
                        fontWeight = FontWeight.ExtraBold,
                        color = DeepBlack,
                        fontSize = 15.sp
                    )
                    Text(text = date, fontSize = 12.sp, color = TextGray)
                }
            }
            Text(
                text = "${if (transaction.type == "income") "+" else "-"} ₱${String.format("%.2f", transaction.amount)}",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 16.sp,
                color = if (transaction.type == "income") SuccessGreen else ErrorRed
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionDialog(selectedTab: Int, onDismiss: () -> Unit, onSave: (Double, String, String) -> Unit) {
    var amount by remember { mutableStateOf("") }
    var type by remember { mutableStateOf(if (selectedTab == 0) "income" else "expense") }
    
    val incomeCategories = listOf(
        stringResource(R.string.cat_salary),
        stringResource(R.string.cat_gift),
        stringResource(R.string.cat_other)
    )
    
    val expenseCategories = listOf(
        stringResource(R.string.cat_food),
        stringResource(R.string.cat_transport),
        stringResource(R.string.cat_bills),
        stringResource(R.string.cat_shopping),
        stringResource(R.string.cat_entertainment),
        stringResource(R.string.cat_health),
        stringResource(R.string.cat_other)
    )
    
    var selectedCategory by remember { mutableStateOf(if (type == "income") incomeCategories[0] else expenseCategories[0]) }
    var expanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (type == "income") stringResource(R.string.add_income) else stringResource(R.string.add_expense), fontWeight = FontWeight.Bold) },
        text = {
            Column {
                OutlinedTextField(
                    value = amount,
                    onValueChange = { if (it.isEmpty() || it.toDoubleOrNull() != null) amount = it },
                    label = { Text(stringResource(R.string.amount)) },
                    modifier = Modifier.fillMaxWidth(),
                    prefix = { Text("₱ ") },
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number)
                )
                
                Spacer(modifier = Modifier.height(20.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(BackgroundBeige.copy(alpha = 0.3f)).padding(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = {
                            type = "income"
                            selectedCategory = incomeCategories[0]
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = if (type == "income") PrimaryRed else Color.Transparent),
                        shape = RoundedCornerShape(8.dp),
                        elevation = null
                    ) {
                        Text(stringResource(R.string.income), color = if (type == "income") White else DeepBlack, fontWeight = FontWeight.Bold)
                    }
                    Button(
                        onClick = {
                            type = "expense"
                            selectedCategory = expenseCategories[0]
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = if (type == "expense") PrimaryRed else Color.Transparent),
                        shape = RoundedCornerShape(8.dp),
                        elevation = null
                    ) {
                        Text(stringResource(R.string.expense), color = if (type == "expense") White else DeepBlack, fontWeight = FontWeight.Bold)
                    }
                }
                
                Spacer(modifier = Modifier.height(20.dp))
                
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = selectedCategory,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(stringResource(R.string.category)) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        val currentCategories = if (type == "income") incomeCategories else expenseCategories
                        currentCategories.forEach { category ->
                            DropdownMenuItem(
                                text = { Text(category) },
                                onClick = {
                                    selectedCategory = category
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { amount.toDoubleOrNull()?.let { onSave(it, type, selectedCategory) } },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryRed),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(stringResource(R.string.save), color = White, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel), color = PrimaryRed, fontWeight = FontWeight.Bold)
            }
        }
    )
}
