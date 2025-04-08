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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.runtime.saveable.rememberSaveable
import com.t.finance.ui.theme.FinanceTheme
import kotlin.math.abs

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

    // Yeni Kar/Zarar hesaplama için değişkenler
    var eskiFiyat by rememberSaveable { mutableStateOf("") }
    var yeniFiyat by rememberSaveable { mutableStateOf("") }
    var karZararHisseAdeti by rememberSaveable { mutableStateOf("") } // Yeni eklenen hisse adeti değişkeni
    var yuzdelikDegisim by rememberSaveable { mutableStateOf(0.0) }
    var toplamMaliyet by rememberSaveable { mutableStateOf("") }
    var hesaplananToplamMaliyet by rememberSaveable { mutableStateOf(0.0) } // Hesaplanan toplam maliyet
    var karZarar by rememberSaveable { mutableStateOf(0.0) }
    var karZararYuzde by rememberSaveable { mutableStateOf(0.0) }

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

        // YENİ KAR/ZARAR HESAPLAMA BÖLÜMÜ
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Kar/Zarar Hesaplama",
                    style = MaterialTheme.typography.titleMedium
                )

                // Hisse fiyat değişimi hesaplama
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = eskiFiyat,
                        onValueChange = {
                            eskiFiyat = it
                            hesaplaYuzdelikDegisim(eskiFiyat, yeniFiyat) { degisim ->
                                yuzdelikDegisim = degisim
                            }
                            // Hisse adeti varsa, otomatik toplam maliyet hesapla
                            hesaplaOtomatikMaliyet(it, karZararHisseAdeti) { maliyet ->
                                hesaplananToplamMaliyet = maliyet
                                if (toplamMaliyet.isEmpty()) {
                                    toplamMaliyet = String.format("%.2f", maliyet)
                                }
                            }
                        },
                        label = { Text("Eski Fiyat (TL)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = yeniFiyat,
                        onValueChange = {
                            yeniFiyat = it
                            hesaplaYuzdelikDegisim(eskiFiyat, yeniFiyat) { degisim ->
                                yuzdelikDegisim = degisim
                            }
                        },
                        label = { Text("Yeni Fiyat (TL)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f)
                    )
                }

                // Yeni hisse adeti girişi
                OutlinedTextField(
                    value = karZararHisseAdeti,
                    onValueChange = {
                        karZararHisseAdeti = it
                        // Hisse adeti değiştiğinde otomatik maliyet hesapla
                        hesaplaOtomatikMaliyet(eskiFiyat, it) { maliyet ->
                            hesaplananToplamMaliyet = maliyet
                            if (toplamMaliyet.isEmpty()) {
                                toplamMaliyet = String.format("%.2f", maliyet)
                            }
                        }
                    },
                    label = { Text("Hisse Adeti") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                // Yüzdelik değişim gösterimi
                val yuzdeIsareti = if (yuzdelikDegisim > 0) "+" else ""
                val yuzdeRenk = when {
                    yuzdelikDegisim > 0 -> MaterialTheme.colorScheme.tertiary
                    yuzdelikDegisim < 0 -> MaterialTheme.colorScheme.error
                    else -> MaterialTheme.colorScheme.onSurface
                }

                Text(
                    text = "Fiyat Değişimi: $yuzdeIsareti${String.format("%.2f", yuzdelikDegisim)}%",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = yuzdeRenk,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Hesaplanan: ${String.format("%.2f", hesaplananToplamMaliyet)} TL",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = yuzdeRenk,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    textAlign = TextAlign.Center
                )

                // Toplam maliyet girişi - Otomatik hesaplanan değeri gösterir veya manuel girişe izin verir
                OutlinedTextField(
                    value = toplamMaliyet,
                    onValueChange = { toplamMaliyet = it },
                    label = { Text("Toplam Maliyet (TL)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        if (hesaplananToplamMaliyet > 0) {
                            Text("Hesaplanan: ${String.format("%.2f", hesaplananToplamMaliyet)} TL")
                        }
                    }
                )

                // Kar/Zarar hesaplama butonu
                Button(
                    onClick = {
                        // Eğer toplam maliyet girilmemişse, hesaplanan maliyet kullanılır
                        val maliyet = if (toplamMaliyet.isNotEmpty()) {
                            toplamMaliyet.toDoubleOrNull() ?: 0.0
                        } else {
                            hesaplananToplamMaliyet
                        }

                        karZararYuzde = yuzdelikDegisim
                        karZarar = maliyet * (yuzdelikDegisim / 100.0)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Text("KAR/ZARAR HESAPLA")
                }

                // Kar/Zarar sonucu
                val karZararDurumu = if (karZarar >= 0) "Kar" else "Zarar"
                val karZararRenk = if (karZarar >= 0) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.error

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = karZararDurumu,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = karZararRenk
                        )

                        Text(
                            text = "${String.format("%.2f", abs(karZarar))} TL",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = karZararRenk
                        )

                        Text(
                            text = "(${String.format("%.2f", karZararYuzde)}%)",
                            style = MaterialTheme.typography.bodyMedium,
                            color = karZararRenk
                        )
                    }
                }
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

// Yüzdelik değişim hesaplama fonksiyonu
private fun hesaplaYuzdelikDegisim(eskiFiyatStr: String, yeniFiyatStr: String, sonuc: (Double) -> Unit) {
    try {
        val eskiFiyat = eskiFiyatStr.toDoubleOrNull() ?: 0.0
        val yeniFiyat = yeniFiyatStr.toDoubleOrNull() ?: 0.0

        if (eskiFiyat > 0) {
            val degisim = ((yeniFiyat - eskiFiyat) / eskiFiyat) * 100.0
            sonuc(degisim)
        } else {
            sonuc(0.0)
        }
    } catch (e: Exception) {
        sonuc(0.0)
    }
}

// Otomatik toplam maliyet hesaplama fonksiyonu
private fun hesaplaOtomatikMaliyet(fiyatStr: String, adetStr: String, sonuc: (Double) -> Unit) {
    try {
        val fiyat = fiyatStr.toDoubleOrNull() ?: 0.0
        val adet = adetStr.toIntOrNull() ?: 0

        val maliyet = fiyat * adet
        sonuc(maliyet)
    } catch (e: Exception) {
        sonuc(0.0)
    }
}