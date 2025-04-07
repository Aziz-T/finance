package com.t.finance

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.runtime.saveable.rememberSaveable
import com.t.finance.ui.theme.FinanceTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            FinanceTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    contentWindowInsets = WindowInsets(0, 0, 0, 0) // EdgeToEdge ile kullanırken gerekli
                ) { paddingValues ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues) // Scaffold'dan gelen system insets
                            .padding(horizontal = 16.dp, vertical = 16.dp) // Ekstra padding
                            ,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        HisseHesaplamaApp()
                    }
                }
            }
        }
    }
}

@Composable
fun HisseHesaplamaApp() {
    var ilkHisseAdeti by rememberSaveable { mutableStateOf("") }
    var ilkHisseFiyati by rememberSaveable { mutableStateOf("") }
    var ilkToplam by rememberSaveable { mutableStateOf(0.0) }

    var ekHisseAdeti by rememberSaveable { mutableStateOf("") }
    var ekHisseFiyati by rememberSaveable { mutableStateOf("") }
    var ekToplam by rememberSaveable { mutableStateOf(0.0) }

    var genelToplam by rememberSaveable { mutableStateOf(0.0) }
    var ortalamaMaliyet by rememberSaveable { mutableStateOf(0.0) }
    var toplamHisseAdeti by rememberSaveable { mutableStateOf(0) }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Hisse Hesaplama Uygulaması",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        // İlk hisse bilgileri
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Mevcut Hisse Bilgileri",
                    style = MaterialTheme.typography.titleMedium
                )

                OutlinedTextField(
                    value = ilkHisseAdeti,
                    onValueChange = {
                        ilkHisseAdeti = it
                        hesaplaIlkToplam(it, ilkHisseFiyati) { toplam ->
                            ilkToplam = toplam
                        }
                    },
                    label = { Text("Hisse Adeti") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = ilkHisseFiyati,
                    onValueChange = {
                        ilkHisseFiyati = it
                        hesaplaIlkToplam(ilkHisseAdeti, it) { toplam ->
                            ilkToplam = toplam
                        }
                    },
                    label = { Text("Hisse Fiyatı (TL)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = "Toplam Maliyet: ${String.format("%.2f", ilkToplam)} TL",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }

        // Ek hisse bilgileri
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Ek Alım Bilgileri",
                    style = MaterialTheme.typography.titleMedium
                )

                OutlinedTextField(
                    value = ekHisseAdeti,
                    onValueChange = {
                        ekHisseAdeti = it
                        hesaplaEkToplam(it, ekHisseFiyati) { toplam ->
                            ekToplam = toplam
                        }
                    },
                    label = { Text("Alınacak Hisse Adeti") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = ekHisseFiyati,
                    onValueChange = {
                        ekHisseFiyati = it
                        hesaplaEkToplam(ekHisseAdeti, it) { toplam ->
                            ekToplam = toplam
                        }
                    },
                    label = { Text("Alınacak Hisse Fiyatı (TL)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = "Ek Alım Maliyeti: ${String.format("%.2f", ekToplam)} TL",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }

        // Hesaplama butonu ve sonuç
        Button(
            onClick = {
                val ilkAdet = ilkHisseAdeti.toIntOrNull() ?: 0
                val ekAdet = ekHisseAdeti.toIntOrNull() ?: 0

                genelToplam = ilkToplam + ekToplam
                toplamHisseAdeti = ilkAdet + ekAdet

                // Ortalama maliyet hesaplama
                ortalamaMaliyet = if (toplamHisseAdeti > 0) {
                    genelToplam / toplamHisseAdeti
                } else {
                    0.0
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text("HESAPLA")
        }

        // Genel toplam ve ortalama maliyet
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "SONUÇLAR",
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = "Genel Toplam: ${String.format("%.2f", genelToplam)} TL",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Ortalama Maliyet: ${String.format("%.2f", ortalamaMaliyet)} TL",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Toplam Hisse Adeti: $toplamHisseAdeti",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // Klavye açıldığında içerik yukarı kaydırılabilsin diye ek bir boşluk
        Spacer(modifier = Modifier.height(100.dp))
    }
}

private fun hesaplaIlkToplam(adet: String, fiyat: String, sonuc: (Double) -> Unit) {
    try {
        val hisseAdeti = adet.toIntOrNull() ?: 0
        val hisseFiyati = fiyat.toDoubleOrNull() ?: 0.0
        sonuc(hisseAdeti * hisseFiyati)
    } catch (e: Exception) {
        sonuc(0.0)
    }
}

private fun hesaplaEkToplam(adet: String, fiyat: String, sonuc: (Double) -> Unit) {
    try {
        val hisseAdeti = adet.toIntOrNull() ?: 0
        val hisseFiyati = fiyat.toDoubleOrNull() ?: 0.0
        sonuc(hisseAdeti * hisseFiyati)
    } catch (e: Exception) {
        sonuc(0.0)
    }
}