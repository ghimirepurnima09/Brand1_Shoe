package com.example.brand_shoe

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.filled.AddAPhoto
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.example.brand_shoe.Model.ProductModel
import com.example.brand_shoe.ViewModel.ProductViewModel
import com.example.brand_shoe.ViewModel.ProductViewModelFactory
import com.example.brand_shoe.repo.ImageRepoImpl
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
    val viewModel: ProductViewModel = viewModel(
        factory = ProductViewModelFactory(ProductRepoImpl(), ImageRepoImpl())
    )
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
            viewModel = viewModel,
            existing = editingProduct,
            onDismiss = { showDialog = false },
            onSave = { name, price, description, imageUrl, stock ->
                val current = editingProduct
                if (current == null) {
                    viewModel.addProduct(ProductModel(name = name, price = price, description = description, imageUrl = imageUrl, stock = stock)) { _, message ->
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                        showDialog = false
                    }
                } else {
                    val updated = current.copy(name = name, price = price, description = description, imageUrl = imageUrl, stock = stock)
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
            ProductImage(
                imageUrl = product.imageUrl,
                contentDescription = product.name,
                modifier = Modifier.size(64.dp).clip(RoundedCornerShape(12.dp))
            )
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
fun ProductFormDialog(
    viewModel: ProductViewModel,
    existing: ProductModel?,
    onDismiss: () -> Unit,
    onSave: (String, Double, String, String, Int) -> Unit
) {
    val context = LocalContext.current
    var name by remember { mutableStateOf(existing?.name ?: "") }
    var price by remember { mutableStateOf(existing?.price?.toString() ?: "") }
    var description by remember { mutableStateOf(existing?.description ?: "") }
    var stock by remember { mutableStateOf(existing?.stock?.toString() ?: "") }
    var existingImageUrl by remember { mutableStateOf(existing?.imageUrl ?: "") }
    var pickedImageUri by remember { mutableStateOf<Uri?>(null) }
    var isSaving by remember { mutableStateOf(false) }

    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) pickedImageUri = uri
    }

    AlertDialog(
        onDismissRequest = { if (!isSaving) onDismiss() },
        title = { Text(if (existing == null) "Add Product" else "Edit Product") },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(10.dp)) {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .clickable {
                            pickImageLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                        }
                ) {
                    when {
                        pickedImageUri != null -> {
                            AsyncImage(
                                model = pickedImageUri,
                                contentDescription = "Selected photo",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                        existingImageUrl.isNotBlank() -> {
                            ProductImage(imageUrl = existingImageUrl, contentDescription = "Product photo", modifier = Modifier.fillMaxSize())
                        }
                        else -> {
                            Column(
                                modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surfaceVariant),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(Icons.Default.AddAPhoto, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                Spacer(modifier = Modifier.height(6.dp))
                                Text("Tap to choose a photo", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
                if (pickedImageUri != null || existingImageUrl.isNotBlank()) {
                    TextButton(onClick = {
                        pickImageLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    }) {
                        Text("Change photo")
                    }
                }

                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = price, onValueChange = { price = it }, label = { Text("Price") }, singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = stock, onValueChange = { stock = it }, label = { Text("Stock") }, singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Description") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            TextButton(
                enabled = !isSaving,
                onClick = {
                    if (name.isBlank()) return@TextButton
                    val finalPrice = price.toDoubleOrNull() ?: 0.0
                    val finalStock = stock.toIntOrNull() ?: 0
                    val uriToUpload = pickedImageUri

                    if (uriToUpload != null) {
                        isSaving = true
                        viewModel.uploadImage(context, uriToUpload) { success, result ->
                            isSaving = false
                            if (success) {
                                onSave(name, finalPrice, description, result, finalStock)
                            } else {
                                Toast.makeText(context, result, Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        onSave(name, finalPrice, description, existingImageUrl, finalStock)
                    }
                }
            ) {
                Text(if (isSaving) "UPLOADING..." else "Save")
            }
        },
        dismissButton = {
            TextButton(enabled = !isSaving, onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun AdminProductPreview() {
    Brand_ShoeTheme { AdminProductScreen(onBackClick = {}) }
}