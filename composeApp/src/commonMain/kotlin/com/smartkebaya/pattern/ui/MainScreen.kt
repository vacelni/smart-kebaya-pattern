package com.smartkebaya.pattern.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.smartkebaya.pattern.model.MeasurementField
import com.smartkebaya.pattern.util.PlatformUtil
import com.smartkebaya.pattern.viewmodel.KebayaViewModel
import smartkebayapatterngenerator.composeapp.generated.resources.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun MainScreen(viewModel: KebayaViewModel = viewModel()) {
    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var selectedTab by remember { mutableStateOf(0) }
    var fieldGuideToShow by remember { mutableStateOf<MeasurementField?>(null) }

    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    if (state.isPrinting) {
        PrintLayout(state, viewModel)
    } else {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                TopAppBar(
                    title = { 
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 8.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f),
                                modifier = Modifier.size(36.dp)
                            ) {
                                Image(
                                    painter = painterResource(Res.drawable.ic_app_logo),
                                    contentDescription = null,
                                    modifier = Modifier.padding(4.dp)
                                )
                            }
                            Spacer(Modifier.width(12.dp))
                            Column {
                                Text(
                                    "Kebaya App", 
                                    fontWeight = FontWeight.ExtraBold, 
                                    color = MaterialTheme.colorScheme.onSurface, 
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    "Pattern Generator", 
                                    style = MaterialTheme.typography.labelSmall, 
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            }
                        }
                    },
                    actions = {
                        Surface(
                            onClick = { viewModel.toggleUnit() },
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.height(36.dp)
                        ) {
                            Box(
                                modifier = Modifier.padding(horizontal = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    state.unit.name,
                                    fontWeight = FontWeight.Black,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }

                        Spacer(Modifier.width(8.dp))

                        Surface(
                            onClick = { 
                                scope.launch {
                                    PlatformUtil.downloadGuide()
                                }
                            },
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                            modifier = Modifier.height(36.dp)
                        ) {
                            Box(
                                modifier = Modifier.padding(horizontal = 12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "PANDUAN",
                                    fontWeight = FontWeight.Black,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.primary,
                                    letterSpacing = 0.5.sp
                                )
                            }
                        }

                        Spacer(Modifier.width(8.dp))

                        IconButton(
                            onClick = { viewModel.toggleTheme() },
                            modifier = Modifier
                                .size(36.dp)
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), CircleShape)
                        ) {
                            Icon(
                                if (state.isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                                contentDescription = "Toggle theme",
                                modifier = Modifier.size(18.dp),
                                tint = if (state.isDarkTheme) Color(0xFFFFB703) else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Spacer(Modifier.width(12.dp))
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        titleContentColor = MaterialTheme.colorScheme.onSurface,
                        actionIconContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                Column(Modifier.padding(20.dp)) {
                    if (PlatformUtil.isWeb) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(modifier = Modifier.weight(1f)) {
                                SectionCard(title = "1. Capaian Pembelajaran (CP)") {
                                    Image(
                                        painter = painterResource(Res.drawable.desain1),
                                        contentDescription = "Capaian Pembelajaran",
                                        modifier = Modifier.fillMaxWidth().height(220.dp).background(Color.White, RoundedCornerShape(8.dp)),
                                        contentScale = ContentScale.Fit
                                    )
                                }
                            }
                            Box(modifier = Modifier.weight(1f)) {
                                SectionCard(title = "2. Desain Ilustrasi") {
                                    Image(
                                        painter = painterResource(Res.drawable.desain_ilustrasi),
                                        contentDescription = "Desain Ilustrasi",
                                        modifier = Modifier.fillMaxWidth().height(220.dp).background(Color.White, RoundedCornerShape(8.dp)),
                                        contentScale = ContentScale.Fit
                                    )
                                }
                            }
                            Box(modifier = Modifier.weight(1f)) {
                                SectionCard(title = "3. Desain Kontruksi") {
                                    Image(
                                        painter = painterResource(Res.drawable.desain_kontruksi),
                                        contentDescription = "Desain Kontruksi",
                                        modifier = Modifier.fillMaxWidth().height(220.dp).background(Color.White, RoundedCornerShape(8.dp)),
                                        contentScale = ContentScale.Fit
                                    )
                                }
                            }
                        }
                    } else {
                        SectionCard(title = "1. Capaian Pembelajaran (CP)") {
                            Image(
                                painter = painterResource(Res.drawable.desain1),
                                contentDescription = "Capaian Pembelajaran",
                                modifier = Modifier.fillMaxWidth().heightIn(max = 400.dp).background(Color.White, RoundedCornerShape(12.dp)),
                                contentScale = ContentScale.Fit
                            )
                        }
                        SectionCard(title = "2. Desain Ilustrasi") {
                            Image(
                                painter = painterResource(Res.drawable.desain_ilustrasi),
                                contentDescription = "Desain Ilustrasi",
                                modifier = Modifier.fillMaxWidth().heightIn(max = 400.dp).background(Color.White, RoundedCornerShape(12.dp)),
                                contentScale = ContentScale.Fit
                            )
                        }
                        SectionCard(title = "3. Desain Kontruksi") {
                            Image(
                                painter = painterResource(Res.drawable.desain_kontruksi),
                                contentDescription = "Desain Kontruksi",
                                modifier = Modifier.fillMaxWidth().heightIn(max = 400.dp).background(Color.White, RoundedCornerShape(12.dp)),
                                contentScale = ContentScale.Fit
                            )
                        }
                    }

                    Spacer(Modifier.height(32.dp))
                    
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.size(6.dp, 24.dp).background(MaterialTheme.colorScheme.primary, RoundedCornerShape(3.dp)))
                        Spacer(Modifier.width(12.dp))
                        Text("4. INPUT PENGUKURAN", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black)
                    }
                    Spacer(Modifier.height(16.dp))

                    if (PlatformUtil.isWeb) {
                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            maxItemsInEachRow = 5
                        ) {
                            MeasurementField.entries.forEach { field ->
                                Box(modifier = Modifier.widthIn(min = 150.dp).weight(1f).padding(bottom = 8.dp)) {
                                    MeasurementInputField(
                                        field = field,
                                        value = state.rawInputs[field] ?: "",
                                        onValueChange = { viewModel.onInputChange(field, it) },
                                        onHelpClick = { fieldGuideToShow = field }
                                    )
                                }
                            }
                        }
                    } else {
                        MeasurementField.entries.forEach { field ->
                            MeasurementInputField(
                                field = field,
                                value = state.rawInputs[field] ?: "",
                                onValueChange = { viewModel.onInputChange(field, it) },
                                onHelpClick = { fieldGuideToShow = field }
                            )
                        }
                    }

                    Spacer(Modifier.height(32.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = { viewModel.generatePattern() },
                            modifier = Modifier.weight(1.5f).height(56.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Text("GENERATE POLA", fontWeight = FontWeight.Black, letterSpacing = 1.sp)
                        }
                        
                        OutlinedButton(
                            onClick = { viewModel.reset() },
                            modifier = Modifier.weight(1f).height(56.dp),
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(2.dp, MaterialTheme.colorScheme.outlineVariant)
                        ) {
                            Text("RESET", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary)
                        }
                    }

                    state.patternSet?.let { patternSet ->
                        Spacer(Modifier.height(48.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(Modifier.size(6.dp, 24.dp).background(MaterialTheme.colorScheme.primary, RoundedCornerShape(3.dp)))
                            Spacer(Modifier.width(12.dp))
                            Text("HASIL GENERATE POLA", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.onSurface)
                        }
                        Spacer(Modifier.height(20.dp))
                        
                        val tabs = listOf("Depan", "Belakang", "Lengan", "Kutubaru")
                        ScrollableTabRow(
                            selectedTabIndex = selectedTab,
                            edgePadding = 0.dp,
                            containerColor = Color.Transparent,
                            divider = {},
                            indicator = { tabPositions ->
                                TabRowDefaults.SecondaryIndicator(
                                    Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                                    color = MaterialTheme.colorScheme.primary,
                                    height = 3.dp
                                )
                            }
                        ) {
                            tabs.forEachIndexed { index, title ->
                                Tab(
                                    selected = selectedTab == index,
                                    onClick = { selectedTab = index },
                                    text = { 
                                        Text(
                                            title, 
                                            fontWeight = if (selectedTab == index) FontWeight.Black else FontWeight.Bold,
                                            color = if (selectedTab == index) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                                        ) 
                                    }
                                )
                            }
                        }
                        
                        Spacer(Modifier.height(24.dp))
                        val (drawing, imageRes) = when (selectedTab) {
                            0 -> patternSet.front to Res.drawable.pola_depan
                            1 -> patternSet.back to Res.drawable.pola_belakang
                            2 -> patternSet.sleeve to Res.drawable.pola_lengan
                            else -> patternSet.kutubaru to Res.drawable.pola_kutubaru
                        }
                        
                        PatternCanvasCard(
                            drawing = drawing,
                            imageRes = imageRes,
                            unit = state.unit
                        )

                        Spacer(Modifier.height(24.dp))
                        Button(
                            onClick = {
                                if (PlatformUtil.isWeb) {
                                    viewModel.setPrinting(true)
                                } else {
                                    PlatformUtil.printPattern(patternSet, state.inputs, state.unit)
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(60.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                        ) {
                            Text("CETAK PDF SEKARANG", fontWeight = FontWeight.Black, letterSpacing = 1.sp)
                        }
                        Spacer(Modifier.height(40.dp))
                    }
                }
            }
        }
    }

    fieldGuideToShow?.let { field ->
        AlertDialog(
            onDismissRequest = { fieldGuideToShow = null },
            title = { 
                Text(
                    field.label, 
                    fontWeight = FontWeight.Black,
                    style = MaterialTheme.typography.titleLarge
                ) 
            },
            text = { 
                Text(
                    field.guide,
                    style = MaterialTheme.typography.bodyLarge
                ) 
            },
            confirmButton = {
                TextButton(onClick = { fieldGuideToShow = null }) {
                    Text("MENGERTI", fontWeight = FontWeight.Bold)
                }
            },
            shape = RoundedCornerShape(24.dp),
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
            textContentColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun PrintLayout(state: com.smartkebaya.pattern.viewmodel.KebayaUiState, viewModel: KebayaViewModel) {
    val patternSet = state.patternSet ?: return
    val patterns = listOf(
        patternSet.front to Res.drawable.pola_depan,
        patternSet.back to Res.drawable.pola_belakang,
        patternSet.sleeve to Res.drawable.pola_lengan,
        patternSet.kutubaru to Res.drawable.pola_kutubaru
    )

    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(1000)
        PlatformUtil.printPattern(patternSet, state.inputs, state.unit)
        viewModel.setPrinting(false)
    }

    Column(modifier = Modifier.fillMaxWidth().background(Color.White)) {
        patterns.forEach { (drawing, res) ->
            // Halaman 1: Gambar Pola
            Box(modifier = Modifier.fillMaxWidth().height(1122.dp).padding(40.dp)) { // A4 Height approx
                Column(modifier = Modifier.fillMaxSize()) {
                    Text(drawing.title.uppercase(), fontWeight = FontWeight.Black, style = MaterialTheme.typography.headlineMedium)
                    Spacer(Modifier.height(8.dp))
                    HorizontalDivider(thickness = 2.dp, color = Color.Black)
                    Spacer(Modifier.height(40.dp))
                    PatternCanvasCard(drawing, res, state.unit, Modifier.weight(1f).fillMaxWidth())
                }
            }
            // Halaman 2: Tabel Rincian
            Box(modifier = Modifier.fillMaxWidth().height(1122.dp).padding(40.dp)) {
                Column(modifier = Modifier.fillMaxSize()) {
                    Text("RINCIAN UKURAN: ${drawing.title.uppercase()}", fontWeight = FontWeight.Black, style = MaterialTheme.typography.headlineSmall)
                    Spacer(Modifier.height(8.dp))
                    HorizontalDivider(thickness = 1.dp, color = Color.Gray)
                    Spacer(Modifier.height(40.dp))
                    
                    // Tabel 2 Kolom identik Android
                    drawing.allValues.entries.chunked(2).forEach { rowEntries ->
                        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            rowEntries.forEach { (label, value) ->
                                val factor = if (state.unit == com.smartkebaya.pattern.model.UnitSystem.INCH) 1 / 2.54 else 1.0
                                val displayValue = value * factor
                                val unitLabel = if (state.unit == com.smartkebaya.pattern.model.UnitSystem.INCH) "in" else "cm"
                                Surface(
                                    modifier = Modifier.weight(1f),
                                    color = Color(0xFFF5F5F5),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Row(Modifier.padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text(label, fontWeight = FontWeight.Bold)
                                        Text("${((displayValue * 100).toInt() / 100.0)} $unitLabel")
                                    }
                                }
                            }
                            if (rowEntries.size == 1) Spacer(Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MeasurementInputField(
    field: MeasurementField,
    value: String,
    onValueChange: (String) -> Unit,
    onHelpClick: () -> Unit
) {
    Column(modifier = Modifier.padding(vertical = 10.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "${field.label.uppercase()} (${field.abbr})",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(Modifier.width(2.dp))
            IconButton(
                onClick = onHelpClick,
                modifier = Modifier.size(20.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.HelpOutline,
                    contentDescription = "Help",
                    modifier = Modifier.size(12.dp),
                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                )
            }
        }
        Spacer(Modifier.height(4.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface
            ),
            textStyle = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
fun SectionCard(title: String, content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(Modifier.padding(12.dp)) {
            Text(
                title, 
                style = MaterialTheme.typography.labelMedium, 
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(Modifier.height(8.dp))
            content()
        }
    }
}
