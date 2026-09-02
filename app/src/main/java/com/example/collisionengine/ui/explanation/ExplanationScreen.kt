package com.example.collisionengine.ui.explanation

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.collisionengine.data.network.LocalDatasetClient
import com.example.collisionengine.ui.theme.BackgroundLight
import com.example.collisionengine.ui.theme.PrimaryBlue

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ExplanationScreen(
    name: String,
    role: String,
    reason: String,
    score: Int,
    onNavigateBack: () -> Unit,
    onStartConversation: () -> Unit
) {
    val context = LocalContext.current
    
    val studentProfile = LocalDatasetClient.getStudentByName(name)
    val facultyProfile = LocalDatasetClient.getFacultyByName(name)
    
    val decodedReason = try {
        java.net.URLDecoder.decode(reason, "UTF-8")
    } catch (e: Exception) {
        reason
    }

    val initials = name.split(" ").mapNotNull { it.firstOrNull()?.toString() }.take(2).joinToString("").uppercase()

    Scaffold(
        containerColor = BackgroundLight,
        topBar = {
            TopAppBar(
                title = { Text("Profile", fontWeight = FontWeight.Bold, color = Color(0xFF1D1D1F)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color(0xFF1D1D1F))
                    }
                },
                actions = {
                    IconButton(onClick = { /* TODO */ }) {
                        Icon(Icons.Outlined.BookmarkBorder, contentDescription = "Bookmark", tint = Color(0xFF1D1D1F))
                    }
                    IconButton(onClick = { /* TODO */ }) {
                        Icon(Icons.Default.Share, contentDescription = "Share", tint = Color(0xFF1D1D1F))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BackgroundLight
                )
            )
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BackgroundLight)
                    .padding(16.dp)
            ) {
                Button(
                    onClick = onStartConversation,
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Send, contentDescription = "Connect", tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Start Conversation", fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            
            // 1. Profile Header Card (White Card)
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(PrimaryBlue.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = initials,
                            style = MaterialTheme.typography.headlineMedium,
                            color = PrimaryBlue,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = name,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1D1D1F)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.BusinessCenter, 
                            contentDescription = "Role", 
                            tint = PrimaryBlue,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = role,
                            style = MaterialTheme.typography.bodyMedium,
                            color = PrimaryBlue,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Quick Actions
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedButton(
                            onClick = {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.linkedin.com/search/results/people/?keywords=$name"))
                                context.startActivity(intent)
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, PrimaryBlue.copy(alpha = 0.3f))
                        ) {
                            Icon(Icons.Default.Share, contentDescription = "LinkedIn", tint = PrimaryBlue, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("LinkedIn", color = PrimaryBlue, fontWeight = FontWeight.Bold)
                        }
                        
                        OutlinedButton(
                            onClick = {
                                val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:"))
                                intent.putExtra(Intent.EXTRA_SUBJECT, "Connecting via CampusConnect")
                                context.startActivity(intent)
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, PrimaryBlue.copy(alpha = 0.3f))
                        ) {
                            Icon(Icons.Default.Email, contentDescription = "Email", tint = PrimaryBlue, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Email", color = PrimaryBlue, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 2. Match Score Banner
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = PrimaryBlue.copy(alpha = 0.05f)),
                border = BorderStroke(1.dp, PrimaryBlue.copy(alpha = 0.2f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(PrimaryBlue),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Star,
                            contentDescription = "Match",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = "98% High Match",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1D1D1F),
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "Matched based on strong technical domain overlap with your queries.",
                            color = Color(0xFF757575),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            
            // 3. Featured Projects
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Code,
                    contentDescription = "Projects",
                    tint = PrimaryBlue,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Featured Projects",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1D1D1F),
                    style = MaterialTheme.typography.titleMedium
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            val projectStr = studentProfile?.projects ?: facultyProfile?.publications ?: decodedReason
            val projectsList = projectStr.split(Regex("[,;]")).map { it.trim() }.filter { it.isNotBlank() && it != "N/A" }
            
            projectsList.forEach { proj ->
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(PrimaryBlue.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = "Check",
                                tint = PrimaryBlue,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = proj,
                            color = Color(0xFF1D1D1F),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            
            // 4. Skills & Technologies Section
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Skills & Technologies",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color(0xFF1D1D1F),
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    val skillStr = studentProfile?.skills ?: facultyProfile?.expertise ?: ""
                    val items = skillStr.split(",").map { it.trim() }.filter { it.isNotBlank() && it != "N/A" }
                    
                    if (items.isNotEmpty()) {
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items.forEach { item ->
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(Color(0xFFF2F2F7))
                                        .padding(horizontal = 12.dp, vertical = 8.dp)
                                ) {
                                    Text(
                                        text = item,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color(0xFF1D1D1F),
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    } else {
                        Text("No specific skills listed.", color = Color.Gray, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 5. Collaboration Benefit
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFE5E5EA)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("💡") // Lightbulb emoji
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Collaboration Benefit",
                            style = MaterialTheme.typography.titleSmall,
                            color = PrimaryBlue,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Connecting with $name can help you resolve technical blockers, exchange research insights, and accelerate project delivery.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF757575)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            
            // 6. Feedback Section
            var feedbackState by androidx.compose.runtime.remember { androidx.compose.runtime.mutableIntStateOf(0) }
            
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Was this match helpful?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF757575),
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = { feedbackState = 1 },
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (feedbackState == 1) PrimaryBlue.copy(alpha = 0.1f) else Color.Transparent
                        ),
                        border = BorderStroke(1.dp, if (feedbackState == 1) PrimaryBlue else Color(0xFFE5E5EA))
                    ) {
                        Text("👍 Yes", color = Color(0xFF1D1D1F))
                    }
                    
                    Spacer(modifier = Modifier.width(16.dp))
                    
                    OutlinedButton(
                        onClick = { feedbackState = -1 },
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (feedbackState == -1) MaterialTheme.colorScheme.errorContainer else Color.Transparent
                        ),
                        border = BorderStroke(1.dp, if (feedbackState == -1) MaterialTheme.colorScheme.error else Color(0xFFE5E5EA))
                    ) {
                        Text("👎 No", color = Color(0xFF1D1D1F))
                    }
                }
                
                Spacer(modifier = Modifier.height(80.dp)) // padding for bottom bar
            }
        }
    }
}
