package com.example.study

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.study.data.LogCSV
import com.example.study.data.SettingsDataStore
import com.example.study.model.ControlViewModel
import com.example.study.model.HorizontalViewModel
import com.example.study.model.TwoDViewModel
import com.example.study.model.VerticalViewModel
import com.example.study.ui.theme.StudyTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


@ExperimentalMaterial3Api
class MainActivity : ComponentActivity() {

    private lateinit var logCSV : LogCSV

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        logCSV = LogCSV()

        setContent {
            StudyTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainLayout()
                }
            }
        }
    }

    @Composable
    fun DrawerItem(item: NavDrawerItem, selected: Boolean, onItemClick: (NavDrawerItem) -> Unit) {
        val background = if (selected) R.color.purple_200 else android.R.color.transparent
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = { onItemClick(item) })
                .height(45.dp)
                .background(colorResource(id = background))
                .padding(start = 10.dp)
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = item.title,
                modifier = Modifier
                    .height(35.dp)
                    .width(35.dp)
            )
            Spacer(modifier = Modifier.width(7.dp))
            Text(
                text = item.title,
                fontSize = 18.sp,
                color = Color.White
            )
        }
    }

    @Composable
    fun ComposeNavigation(navController: NavHostController) {
        val context = LocalContext.current
        val settingsStore = SettingsDataStore(context, navController)
        logCSV.setSettingsDataStore(settingsStore)

        // init model and start getting data
        val controlViewModel = ControlViewModel(settingsStore)
        val verticalViewModel = VerticalViewModel(logCSV)
        val horizontalViewModel = HorizontalViewModel(logCSV)
        val viewModel2D = TwoDViewModel(logCSV)

        NavHost(navController, startDestination = NavDrawerItem.Home.route) {
            composable(NavDrawerItem.Home.route) {
                HomeScreen(verticalViewModel, horizontalViewModel, viewModel2D, settingsStore, logCSV)
            }
            composable(NavDrawerItem.Vertical.route) {
                SelectVerticalApp(controlViewModel, verticalViewModel, settingsStore, logCSV)
            }
            composable(NavDrawerItem.Horizontal.route) {
                SelectHorizontalApp(controlViewModel, horizontalViewModel, settingsStore, logCSV)
            }
            composable(NavDrawerItem.TwoDimensional.route) {
                Select2DApp(viewModel2D, controlViewModel, settingsStore, logCSV)
            }
            composable(NavDrawerItem.TwoDimensionalFS.route) {
                Select2DAppFS(viewModel2D, settingsStore, logCSV)
            }
            composable(NavDrawerItem.Settings.route) {
                Settings(verticalViewModel, horizontalViewModel, viewModel2D, settingsStore)
            }
            composable(NavDrawerItem.LogUDP.route) {
                UDP_Log(controlViewModel.controlUIState)
            }
        }
    }

    @Composable
    fun DrawerLayout(scope: CoroutineScope, drawerState: DrawerState, navController: NavController) {
        val items = listOf(
            NavDrawerItem.Home,
            NavDrawerItem.Vertical,
            NavDrawerItem.Horizontal,
            NavDrawerItem.TwoDimensional,
            NavDrawerItem.TwoDimensionalFS,
            NavDrawerItem.Settings,
            NavDrawerItem.LogUDP
        )
        Column {
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
            )
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route
            items.forEach { item ->
                DrawerItem(item = item, selected = currentRoute == item.route, onItemClick = {
                    navController.navigate(item.route) {
                        navController.graph.startDestinationRoute?.let { route ->
                            popUpTo(route) {
                                saveState = true
                            }
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                    scope.launch {
                        drawerState.close()
                    }
                })
            }
        }
    }

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @ExperimentalMaterial3Api
    @Composable
    fun MainLayout() {
        val drawerState = rememberDrawerState(DrawerValue.Closed)
        val scope = rememberCoroutineScope()
        val navController = rememberNavController()

        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet {
                    DrawerLayout(scope = scope, drawerState = drawerState, navController = navController)
                }
            },
            content = {
                Scaffold(
                    topBar = { TopBar(scope = scope, drawerState = drawerState) }
                ) {
                    ComposeNavigation(navController = navController)
                }
            }
        )
    }
}