package com.example.collisionengine.ui.explanation

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Star
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

@OptIn(ExperimentalMaterial3Api::class)
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
    
    // Lookup the full profile from the dataset
    val studentProfile = LocalDatasetClient.getStudentByName(name)
    val facultyProfile = LocalDatasetClient.getFacultyByName(name)
    
    val decodedReason = try {
        java.net.URLDecoder.decode(reason, "UTF-8")
    } catch (e: Exception) {
        reason
    }

    Scaffold(
        containerColor = BackgroundLight,
        topBar = {
            TopAppBar(
                title = { Text("Profile", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BackgroundLight
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onStartConversation,
                icon = { Icon(Icons.Default.Send, contentDescription = "Connect") },
                text = { Text("Start Conversation", fontWeight = FontWeight.Bold) },
                containerColor = Color(0xFFE8DEF8),
                contentColor = Color(0xFF1D192B),
                modifier = Modifier.padding(bottom = 16.dp)
            )
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            // Profile Header
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE8DEF8)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = name.firstOrNull()?.toString() ?: "?",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color(0xFF1D192B),
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = name,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1D192B)
                    )
                    Text(
                        text = role,
                        style = MaterialTheme.typography.titleMedium,
                        color = PrimaryBlue,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Match Score Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFE8DEF8))
                    .padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Star,
                        contentDescription = "Match",
                        tint = Color(0xFF1D192B),
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = "98% Match Score",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1D192B),
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "Based on structural overlap in your goals and experiences.",
                            color = Color(0xFF49454F),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            
            // Dynamic Dataset Profile Info
            if (studentProfile != null) {
                ProfileSection(title = "Projects & Experience:", content = studentProfile.projects ?: "")
                ProfileSection(title = "Skills:", content = studentProfile.skills ?: "")
            } else if (facultyProfile != null) {
                ProfileSection(title = "Research Interests:", content = facultyProfile.researchInterests ?: "")
                ProfileSection(title = "Publications & Expertise:", content = facultyProfile.expertise ?: "")
            } else {
                ProfileSection(title = "Projects & Experience:", content = decodedReason)
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            HorizontalDivider(color = Color.LightGray)
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Connecting with $name can help you avoid common pitfalls and accelerate your progress.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Quick Actions
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "LinkedIn",
                    color = PrimaryBlue,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(PrimaryBlue.copy(alpha = 0.1f))
                        .clickable {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.linkedin.com/search/results/people/?keywords=$name"))
                            context.startActivity(intent)
                        }
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                )
                Text(
                    text = "Email",
                    color = PrimaryBlue,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(PrimaryBlue.copy(alpha = 0.1f))
                        .clickable {
                            val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:"))
                            intent.putExtra(Intent.EXTRA_SUBJECT, "Connecting via CampusConnect")
                            context.startActivity(intent)
                        }
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Feedback Section
            var feedbackState by androidx.compose.runtime.remember { androidx.compose.runtime.mutableIntStateOf(0) }
            
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Was this match helpful?",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color(0xFF1D192B),
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = { feedbackState = 1 },
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (feedbackState == 1) Color(0xFFE8DEF8) else Color.Transparent
                        )
                    ) {
                        Text("?? Yes", color = Color(0xFF1D192B))
                    }
                    
                    Spacer(modifier = Modifier.width(16.dp))
                    
                    OutlinedButton(
                        onClick = { feedbackState = -1 },
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (feedbackState == -1) MaterialTheme.colorScheme.errorContainer else Color.Transparent
                        )
                    ) {
                        Text("?? No", color = Color(0xFF1D192B))
                    }
                }
                
                Spacer(modifier = Modifier.height(100.dp)) // padding for FAB
            }
        }
    }
}

@Composable
fun ProfileSection(title: String, content: String) {
    if (content.isBlank() || content == "N/A") return
    
    val items = content.split(",").map { 
        try {
            java.net.URLDecoder.decode(it, "UTF-8").trim()
        } catch (e: Exception) {
            it.trim()
        }
    }.filter { it.isNotBlank() }
    
    Column(modifier = Modifier.padding(bottom = 16.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = Color(0xFF1D192B),
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        
        // Use FlowRow for chips
        @OptIn(ExperimentalLayoutApi::class)
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items.forEach { item ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.White)
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = item,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF49454F)
                    )
                }
            }
        }
    }
}
