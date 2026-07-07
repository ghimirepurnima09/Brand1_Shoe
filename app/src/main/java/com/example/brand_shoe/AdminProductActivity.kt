package com.example.brand_shoe

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.brand_shoe.Model.ProductModel
import com.example.brand_shoe.ViewModel.ProductViewModel
import com.example.brand_shoe.ViewModel.ProductViewModelFactory
import com.example.brand_shoe.repo.ProductRepoImpl
import com.example.brand_shoe.ui.theme.Brand_ShoeTheme

class AdminProductActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Brand_ShoeTheme {
                AdminProductScreen(onBackClick = { finish() })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminProductScreen(onBackClick: () -> Unit) {
    val context = LocalContext.current
    val viewModel: ProductViewModel = viewModel(factory = ProductViewModelFactory(ProductRepoImpl()))
    var products by remember { mutableStateOf<List<ProductModel?>>(emptyList()) }
    var showDialog by remember { mutableStateOf(false) }
    var editingProduct by remember { mutableStateOf<ProductModel?>(null) }

    LaunchedEffect(Unit) {
        viewModel.getAllProducts { _, _, data -> products = data }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Manage Products") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { editingProduct = null; showDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add product")
            }
        }
    ) { innerPadding ->
        if (products.isEmpty()) {
            Box(modifier = Modifier.padding(innerPadding).fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No products yet — tap + to add one", color = Color.Gray)
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(innerPadding).fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(products) { product ->
                    product?.let {
                        ProductAdminRow(
                            product = it,
                            onEdit = { editingProduct = it; showDialog = true },
                            onDelete = {
                                viewModel.deleteProduct(it.id) { _, message ->
                                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    if (showDialog) {
        ProductFormDialog(
            existing = editingProduct,
            onDismiss = { showDialog = false },
            onSave = { name, price, description, imageKey, stock ->
                val current = editingProduct
                if (current == null) {
                    viewModel.addProduct(ProductModel(name = name, price = price, description = description, imageKey = imageKey, stock = stock)) { _, message ->
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                        showDialog = false
                    }
                } else {
                    val updated = current.copy(name = name, price = price, description = description, imageKey = imageKey, stock = stock)
                    viewModel.updateProduct(updated.id, updated) { _, message ->
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                        showDialog = false
                    }
                }
            }
        )
    }
}

@Composable
fun ProductAdminRow(product: ProductModel, onEdit: () -> Unit, onDelete: () -> Unit) {
    Card(shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(64.dp).clip(RoundedCornerShape(12.dp)).background(Color(0xFFF1F1F1)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = imageKeyToDrawable(product.imageKey)),
                    contentDescription = product.name,
                    modifier = Modifier.size(48.dp),
                    contentScale = ContentScale.Fit
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(product.name, fontWeight = FontWeight.Bold, maxLines = 1)
                Text("$${"%.2f".format(product.price)} · Stock: ${product.stock}", color = Color.Gray, style = MaterialTheme.typography.bodySmall)
            }
            IconButton(onClick = onEdit) { Icon(Icons.Default.Edit, contentDescription = "Edit") }
            IconButton(onClick = onDelete) { Icon(Icons.Default.Delete, contentDescription = "Delete") }
        }
    }
}

@Composable
fun ProductFormDialog(existing: ProductModel?, onDismiss: () -> Unit, onSave: (String, Double, String, String, Int) -> Unit) {
    var name by remember { mutableStateOf(existing?.name ?: "") }
    var price by remember { mutableStateOf(existing?.price?.toString() ?: "") }
    var description by remember { mutableStateOf(existing?.description ?: "") }
    var stock by remember { mutableStateOf(existing?.stock?.toString() ?: "") }
    var imageKey by remember { mutableStateOf(existing?.imageKey ?: "shoe1") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (existing == null) "Add Product" else "Edit Product") },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = price, onValueChange = { price = it }, label = { Text("Price") }, singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = stock, onValueChange = { stock = it }, label = { Text("Stock") }, singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Description") }, modifier = Modifier.fillMaxWidth())

                Text("Image", fontWeight = FontWeight.Bold)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    availableImageKeys.forEach { key ->
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (imageKey == key) Color(0xFFD0D0D0) else Color(0xFFF1F1F1))
                                .clickable { imageKey = key },
                            contentAlignment = Alignment.Center
                        ) {
                            Image(painter = painterResource(id = imageKeyToDrawable(key)), contentDescription = key, modifier = Modifier.size(32.dp), contentScale = ContentScale.Fit)
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                if (name.isBlank()) return@TextButton
                onSave(name, price.toDoubleOrNull() ?: 0.0, description, imageKey, stock.toIntOrNull() ?: 0)
            }) { Text("Save") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Preview(showBackground = true)
@Composable
fun AdminProductPreview() {
    Brand_ShoeTheme { AdminProductScreen(onBackClick = {}) }
}