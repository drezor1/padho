package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.database.ChatMessageEntity
import com.example.data.database.QuizResultEntity
import com.example.data.database.StudyNoteEntity
import com.example.data.model.Question
import com.example.ui.theme.MathGreen
import com.example.ui.viewmodel.StudyViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: StudyViewModel,
    modifier: Modifier = Modifier
) {
    val activeScreen by viewModel.activeScreen.collectAsStateWithLifecycle()
    val mode by viewModel.mode.collectAsStateWithLifecycle()
    val chatMessages by viewModel.chatMessages.collectAsStateWithLifecycle()
    val isChatLoading by viewModel.isChatLoading.collectAsStateWithLifecycle()

    val quizQuestions by viewModel.quizQuestions.collectAsStateWithLifecycle()
    val isQuizLoading by viewModel.isQuizLoading.collectAsStateWithLifecycle()
    val quizError by viewModel.quizError.collectAsStateWithLifecycle()
    val quizCurrentIndex by viewModel.currentQuestionIndex.collectAsStateWithLifecycle()
    val quizScore by viewModel.quizScore.collectAsStateWithLifecycle()
    val userAnswers by viewModel.userAnswers.collectAsStateWithLifecycle()
    val quizCompleted by viewModel.quizCompleted.collectAsStateWithLifecycle()
    val selectedQuizSubject by viewModel.selectedQuizSubject.collectAsStateWithLifecycle()

    val quizHistory by viewModel.quizHistory.collectAsStateWithLifecycle()
    val studyNotes by viewModel.studyNotes.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Padho App",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Text(
                            text = "Made by Deepak",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Light,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                        )
                    }
                },
                navigationIcon = {
                    Icon(
                        imageVector = Icons.Default.School,
                        contentDescription = "App Icon",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.padding(start = 16.dp, end = 8.dp)
                    )
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.navigateTo("Dashboard") },
                        modifier = Modifier.testTag("action_home_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = "Home",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp,
                modifier = Modifier.testTag("app_bottom_nav")
            ) {
                NavigationBarItem(
                    selected = activeScreen == "Dashboard" || activeScreen == "Quiz",
                    onClick = { viewModel.navigateTo("Dashboard") },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Dashboard") },
                    label = { Text("Padhai") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    ),
                    modifier = Modifier.testTag("nav_dashboard")
                )
                NavigationBarItem(
                    selected = activeScreen == "Chat",
                    onClick = { viewModel.navigateTo("Chat") },
                    icon = { Icon(Icons.Default.Chat, contentDescription = "AI Guru") },
                    label = { Text("AI Mentor") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    ),
                    modifier = Modifier.testTag("nav_chat")
                )
                NavigationBarItem(
                    selected = activeScreen == "Notes",
                    onClick = { viewModel.navigateTo("Notes") },
                    icon = { Icon(Icons.Default.Book, contentDescription = "Notes") },
                    label = { Text("Notes") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    ),
                    modifier = Modifier.testTag("nav_notes")
                )
                NavigationBarItem(
                    selected = activeScreen == "History",
                    onClick = { viewModel.navigateTo("History") },
                    icon = { Icon(Icons.Default.Insights, contentDescription = "Progress") },
                    label = { Text("Progress") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    ),
                    modifier = Modifier.testTag("nav_history")
                )
            }
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.background,
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                        )
                    )
                )
        ) {
            when (activeScreen) {
                "Dashboard" -> DashboardScreen(
                    viewModel = viewModel,
                    currentMode = mode,
                    quizHistory = quizHistory,
                    studyNotes = studyNotes
                )
                "Chat" -> ChatScreen(
                    viewModel = viewModel,
                    currentMode = mode,
                    chatMessages = chatMessages,
                    isLoading = isChatLoading
                )
                "Quiz" -> QuizScreen(
                    viewModel = viewModel,
                    currentMode = mode,
                    subject = selectedQuizSubject,
                    questions = quizQuestions,
                    isLoading = isQuizLoading,
                    error = quizError,
                    currentIndex = quizCurrentIndex,
                    score = quizScore,
                    userAnswers = userAnswers,
                    completed = quizCompleted
                )
                "Notes" -> StudyNotesScreen(
                    viewModel = viewModel,
                    studyNotes = studyNotes
                )
                "History" -> ProgressScreen(
                    viewModel = viewModel,
                    quizHistory = quizHistory
                )
            }
        }
    }
}

@Composable
fun DashboardScreen(
    viewModel: StudyViewModel,
    currentMode: String,
    quizHistory: List<QuizResultEntity>,
    studyNotes: List<StudyNoteEntity>
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Welcome Header
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.9f)
                ),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = "Namaskar, Deepak! 👋",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Padho App me aapka swagat hai. Apna study mode select karein aur behtareen tayyari shuru karein!",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.9f),
                        lineHeight = 20.sp
                    )
                }
            }
        }

        // Mode Switching Component
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .border(
                        1.dp,
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
                        RoundedCornerShape(12.dp)
                    )
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Chuninda Study Mode",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(start = 4.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Mode 1: Entrance Mode
                    Button(
                        onClick = { viewModel.setMode("Entrance") },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (currentMode == "Entrance") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = if (currentMode == "Entrance") MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("mode_entrance_btn")
                    ) {
                        Text(
                            text = "Entrance Exam",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }

                    // Mode 2: Course Mode
                    Button(
                        onClick = { viewModel.setMode("Course") },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (currentMode == "Course") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = if (currentMode == "Course") MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("mode_course_btn")
                    ) {
                        Text(
                            text = "College Course",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }

        // Mode Rules info box
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "info",
                            tint = MaterialTheme.colorScheme.secondary
                        )
                        Text(
                            text = if (currentMode == "Entrance") "CG PRE-D.EL.ED ENTRANCE INFO" else "CG D.EL.ED COLLEGE COURSE INFO",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }

                    Text(
                        text = if (currentMode == "Entrance") {
                            "D.El.Ed entrance clear karne me madad ke liye GK (CG & India), Teaching Aptitude, Mental Ability, aur Hindi/English Grammar modules allowed hain. Core college theory sawal yahan refuse honge."
                        } else {
                            "D.El.Ed College (1st & 2nd Year) exams ke liye Bal Vikas, Pedagogy, Lesson Planning (SCERT) textbooks allowed hain. General GK/Reasoning yahan restricted hain."
                        },
                        fontSize = 13.sp,
                        lineHeight = 18.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                    )
                }
            }
        }

        // Quick Stats row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "${quizHistory.size}",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Quizzes Taken",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }

                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "${studyNotes.size}",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.secondary
                        )
                        Text(
                            text = "Saved Notes",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }
            }
        }

        // Subject list Title
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.MenuBook,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Instant Study Quizzes (5 MCQ)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }

        // List of allowed subjects based on selected mode
        val subjects = if (currentMode == "Entrance") {
            listOf(
                "Chhattisgarh General Knowledge (CG GK)",
                "Indian History & GK",
                "Mental Ability (Reasoning)",
                "Teaching Aptitude (Shikshan Abhiruchi)",
                "General Hindi Grammar (Vyakaran)",
                "General English Grammar"
            )
        } else {
            listOf(
                "Child Development (Bal Vikas)",
                "Pedagogy (Shikshan Shastra)",
                "Lesson Planning (Path Yojna)",
                "SCERT Textbook Syllabus"
            )
        }

        items(subjects) { subject ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.generateStudyQuiz(subject) }
                    .testTag("subject_card_${subject.take(15).replace(" ", "_")}"),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(1.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lightbulb,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Column {
                            Text(
                                text = subject,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Start active quizzing for Deepak",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                            )
                        }
                    }

                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = "Start Quiz",
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        // Pre-loaded Chat prompt helper card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.navigateTo("Chat") },
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f)
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Chat,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary
                    )
                    Column {
                        Text(
                            text = "AI Guru se Padhai Karein!",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.secondary
                        )
                        Text(
                            text = "D.El.Ed ke mushkil concepts ke baare me simple Hinglish me samjhein. AI Mentor screen par jaakar sawal puchein.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ChatScreen(
    viewModel: StudyViewModel,
    currentMode: String,
    chatMessages: List<ChatMessageEntity>,
    isLoading: Boolean
) {
    var textState by remember { mutableStateOf("") }
    val lazyListState = rememberLazyListState()
    val focusManager = LocalFocusManager.current

    LaunchedEffect(chatMessages.size, isLoading) {
        if (chatMessages.isNotEmpty()) {
            lazyListState.animateScrollToItem(chatMessages.size - 1)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        // Chat Header Mode indicator
        Card(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            shape = RoundedCornerShape(8.dp)
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (currentMode == "Entrance") "Entrance Exam Guru (Hinglish)" else "College Course Guru (Hinglish)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = if (currentMode == "Entrance") "CG-GK, India-GK, Mental Ability, English/Hindi, Teaching" else "Bal Vikas, Pedagogy, Lesson Planning, SCERT",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                }

                IconButton(
                    onClick = { viewModel.clearChatHistory() },
                    modifier = Modifier.testTag("clear_chat_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Clear Chat History",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    )
                }
            }
        }

        // Chat list area
        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
            if (chatMessages.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.School,
                        contentDescription = "Welcome logo",
                        tint = MaterialTheme.colorScheme.secondary.copy(alpha = 0.4f),
                        modifier = Modifier.size(72.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Namaskar Deepak!",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Main Padho App ka AI Brain hoon. Aapke chuninda mode (${if (currentMode == "Entrance") "Entrance" else "Course"}) se jude sawal Hinglish me puchein!",
                        textAlign = TextAlign.Center,
                        fontSize = 13.sp,
                        lineHeight = 18.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                    )

                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "Chuninda Suggestion Prompts:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    val helpers = if (currentMode == "Entrance") {
                        listOf(
                            "Chhattisgarh Ke 5 basic GK sawal",
                            "Teaching Aptitude kyu important hai?",
                            "Hindi vyakaran me sandhi kya hai?"
                        )
                    } else {
                        listOf(
                            "Jean Piaget ki theory samjhaiye",
                            "Bal Vikas ka matlab kya hai?",
                            "SCERT Lesson plan kaise banta hai?"
                        )
                    }

                    helpers.forEach { suggestion ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable {
                                    viewModel.sendMessage(suggestion)
                                },
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Text(
                                text = "👉 \"$suggestion\"",
                                modifier = Modifier.padding(12.dp).fillMaxWidth(),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }
                }
            } else {
                LazyColumn(
                    state = lazyListState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(chatMessages) { message ->
                        val isUser = message.role == "user"
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
                        ) {
                            Card(
                                shape = RoundedCornerShape(
                                    topStart = 16.dp,
                                    topEnd = 16.dp,
                                    bottomStart = if (isUser) 16.dp else 4.dp,
                                    bottomEnd = if (isUser) 4.dp else 16.dp
                                ),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
                                ),
                                border = if (isUser) null else BorderStroke(
                                    1.dp,
                                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
                                ),
                                modifier = Modifier
                                    .widthIn(max = 280.dp)
                                    .testTag(if (isUser) "chat_msg_user" else "chat_msg_model")
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        text = message.content,
                                        fontSize = 14.sp,
                                        color = if (isUser) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                                        lineHeight = 20.sp
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(message.timestamp)),
                                        fontSize = 9.sp,
                                        color = (if (isUser) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface).copy(alpha = 0.6f),
                                        modifier = Modifier.align(Alignment.End)
                                    )
                                }
                            }
                        }
                    }

                    if (isLoading) {
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(8.dp),
                                horizontalArrangement = Arrangement.Start,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "AI Guru soch rahe hain...",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Chat input area
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TextField(
                value = textState,
                onValueChange = { textState = it },
                placeholder = { Text("Sawal puchein (e.g. CG me kul kitne districts hain?)...") },
                modifier = Modifier
                    .weight(1f)
                    .testTag("chat_input_text_field"),
                shape = RoundedCornerShape(24.dp),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent
                )
            )

            FloatingActionButton(
                onClick = {
                    if (textState.isNotBlank()) {
                        viewModel.sendMessage(textState)
                        textState = ""
                        focusManager.clearFocus()
                    }
                },
                modifier = Modifier
                    .size(48.dp)
                    .testTag("chat_send_fab"),
                shape = CircleShape,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = "Send Message",
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
fun QuizScreen(
    viewModel: StudyViewModel,
    currentMode: String,
    subject: String,
    questions: List<Question>,
    isLoading: Boolean,
    error: String?,
    currentIndex: Int,
    score: Int,
    userAnswers: List<String>,
    completed: Boolean
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        if (isLoading) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                Text(
                    text = "AI customized MCQs taiyaar ho rahe hain...",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Mode: ${if (currentMode == "Entrance") "Entrance Mode" else "Course Mode"}\nSubject: $subject",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                    textAlign = TextAlign.Center
                )
            }
        } else if (error != null) {
            Card(
                modifier = Modifier.fillMaxWidth().wrapContentHeight(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Cancel,
                        contentDescription = "Error",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(48.dp)
                    )
                    Text(
                        text = "Galti Ho Gayi!",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.error
                    )
                    Text(
                        text = error,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = { viewModel.restartQuiz() },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        modifier = Modifier.testTag("quiz_retry_btn")
                    ) {
                        Text("Dobara Koshish Karein")
                    }

                    TextButton(onClick = { viewModel.navigateTo("Dashboard") }) {
                        Text("Dashboard me wapas jaein")
                    }
                }
            }
        } else if (completed) {
            // Quiz completed screen stats
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Quiz Samapt! 🎉",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = "Badiya Tayyari Deepak!",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "$score / ${questions.size}",
                                fontSize = 48.sp,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.primary
                            )

                            val motivate = when {
                                score == questions.size -> "Shaandar! Aapki padhai kamaal ki hai. Perfect Score! 💯"
                                score >= questions.size - 2 -> "Boht badhiya performance! Thoda sa aur focus karein. 👍"
                                else -> "Koi baat nahi Deepak! Hardwork karte rahein, improve ho jayega. 📖"
                            }

                            Text(
                                text = motivate,
                                fontSize = 13.sp,
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )
                        }
                    }
                }

                item {
                    Text(
                        text = "Detailed Review & Explanations",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.fillMaxWidth().padding(start = 4.dp)
                    )
                }

                items(questions.indices.toList()) { index ->
                    val q = questions[index]
                    val uAns = userAnswers.getOrNull(index) ?: "Not answered"
                    val isCorr = uAns.trim().lowercase() == q.correct_answer.trim().lowercase()

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Q${index + 1}: ${q.question}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (isCorr) Icons.Default.CheckCircle else Icons.Default.Cancel,
                                    contentDescription = null,
                                    tint = if (isCorr) MathGreen else MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = if (isCorr) "Aapka jawab Sahi hai" else "Aapka jawab Galat tha",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isCorr) MathGreen else MaterialTheme.colorScheme.error
                                )
                            }

                            Text(
                                text = "Apna Option: $uAns",
                                fontSize = 12.sp,
                                color = if (isCorr) MathGreen else MaterialTheme.colorScheme.error
                            )

                            Text(
                                text = "Correct Option: ${q.correct_answer}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = MathGreen
                            )

                            Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))

                            Text(
                                text = "Explanation:\n${q.explanation}",
                                fontSize = 12.sp,
                                lineHeight = 16.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                            )

                            // Quick trigger bookmark button
                            OutlinedButton(
                                onClick = {
                                    viewModel.addStudyNote(
                                        subject = subject,
                                        question = q.question,
                                        answer = "Correct Option: ${q.correct_answer}\n\nExplanation: ${q.explanation}"
                                    )
                                },
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.align(Alignment.End),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.secondary)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = "Save Note",
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Save to Notes", fontSize = 11.sp)
                            }
                        }
                    }
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = { viewModel.restartQuiz() },
                            modifier = Modifier.weight(1f).testTag("action_restart_quiz"),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Take Quiz Again")
                        }

                        OutlinedButton(
                            onClick = { viewModel.navigateTo("Dashboard") },
                            modifier = Modifier.weight(1f).testTag("action_to_dashboard"),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Go to Home")
                        }
                    }
                }
            }
        } else if (questions.isNotEmpty() && currentIndex < questions.size) {
            val q = questions[currentIndex]
            var selectedOption by remember { mutableStateOf<String?>(null) }
            var hintVisible by remember { mutableStateOf(false) }
            val quizScrollState = rememberScrollState()

            LaunchedEffect(currentIndex) {
                selectedOption = null
                hintVisible = false
                quizScrollState.scrollTo(0)
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(quizScrollState),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header progress
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "$subject Quiz",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Text(
                        text = "Question ${currentIndex + 1} of ${questions.size}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )
                }

                LinearProgressIndicator(
                    progress = (currentIndex.toFloat() + 1f) / questions.size.toFloat(),
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(4.dp)),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                )

                // Question Area
                Card(
                    modifier = Modifier.fillMaxWidth().wrapContentHeight(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Text(
                            text = q.question,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            lineHeight = 22.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                // Answer Options Block
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    q.options.forEach { opt ->
                        val isAnswered = selectedOption != null
                        val isSelected = opt == selectedOption
                        val isCorrectOpt = opt.trim().lowercase() == q.correct_answer.trim().lowercase()

                        val cardColor = when {
                            !isAnswered -> MaterialTheme.colorScheme.surface
                            isSelected && isCorrectOpt -> MathGreen.copy(alpha = 0.15f)
                            isSelected && !isCorrectOpt -> MaterialTheme.colorScheme.error.copy(alpha = 0.15f)
                            isCorrectOpt -> MathGreen.copy(alpha = 0.15f)
                            else -> MaterialTheme.colorScheme.surface
                        }

                        val borderColor = when {
                            !isAnswered -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                            isSelected && isCorrectOpt -> MathGreen
                            isSelected && !isCorrectOpt -> MaterialTheme.colorScheme.error
                            isCorrectOpt -> MathGreen
                            else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f)
                        }

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(enabled = !isAnswered) {
                                    selectedOption = opt
                                }
                                .border(1.dp, borderColor, RoundedCornerShape(10.dp))
                                .testTag("option_card_${opt.take(10).replace(" ", "_")}"),
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = cardColor)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = opt,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.weight(1f)
                                )

                                if (isAnswered) {
                                    Icon(
                                        imageVector = if (isCorrectOpt) Icons.Default.CheckCircle else if (isSelected) Icons.Default.Cancel else Icons.Default.Info,
                                        contentDescription = null,
                                        tint = if (isCorrectOpt) MathGreen else if (isSelected) MaterialTheme.colorScheme.error else Color.Transparent,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Hint and explanation spoilers
                if (selectedOption == null) {
                    // Hint expandable button before answering
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.Start
                    ) {
                        TextButton(
                            onClick = { hintVisible = !hintVisible },
                            modifier = Modifier.testTag("quiz_hint_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lightbulb,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.secondary
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (hintVisible) "Hint Chupaayein" else "Hint Dekhein", color = MaterialTheme.colorScheme.secondary)
                        }

                        AnimatedVisibility(visible = hintVisible, enter = fadeIn(), exit = fadeOut()) {
                            Card(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f)),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f))
                            ) {
                                Text(
                                    text = "💡Hint Key: ${q.hint}",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(12.dp)
                                )
                            }
                        }
                    }
                } else {
                    // Answer is submitted, exhibit detailed explanation
                    Card(
                        modifier = Modifier.fillMaxWidth().wrapContentHeight(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth()
                        ) {
                            val userIsCorrect = selectedOption?.trim()?.lowercase() == q.correct_answer.trim().lowercase()

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = if (userIsCorrect) Icons.Default.CheckCircle else Icons.Default.Cancel,
                                    contentDescription = null,
                                    tint = if (userIsCorrect) MathGreen else MaterialTheme.colorScheme.error
                                )
                                Text(
                                    text = if (userIsCorrect) "Bahut Badiya Deepak! Sahi Jawab." else "Afsos Galat Jawab! Sahi Answer: ${q.correct_answer}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = if (userIsCorrect) MathGreen else MaterialTheme.colorScheme.error
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = "Sahi Answer Explanation:",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = q.explanation,
                                fontSize = 12.sp,
                                lineHeight = 16.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // bookmark action
                            OutlinedButton(
                                onClick = {
                                    viewModel.addStudyNote(
                                        subject = subject,
                                        question = q.question,
                                        answer = "Sahi Jawab: ${q.correct_answer}\n\nExplanation: ${q.explanation}"
                                    )
                                },
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.align(Alignment.Start)
                            ) {
                                Icon(Icons.Default.Star, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Notes me save karein", fontSize = 11.sp)
                            }
                        }
                    }

                    // Next button
                    Button(
                        onClick = { viewModel.submitQuizAnswer(selectedOption!!) },
                        modifier = Modifier.fillMaxWidth().height(48.dp).testTag("quiz_next_btn"),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = if (currentIndex + 1 >= questions.size) "Results Dekhein" else "Agla Sawaal",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        } else {
            // General backup/restart
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("Kripya quiz generate karne ke liye Dashboard me jaayein.")
                Button(onClick = { viewModel.navigateTo("Dashboard") }) {
                    Text("Go to Dashboard")
                }
            }
        }
    }
}

@Composable
fun StudyNotesScreen(
    viewModel: StudyViewModel,
    studyNotes: List<StudyNoteEntity>
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Deepak's Study Notes 📝",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "${studyNotes.size} Notes Saved",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (studyNotes.isEmpty()) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Book,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Notes empty hain!",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
                )
                Text(
                    text = "Study Quiz lene ke dauraan 'Save to Notes' dabaakar answers bookmark karein taaki aap unhe kabhi bhi offline padh sakein.",
                    textAlign = TextAlign.Center,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 4.dp),
                    lineHeight = 16.sp
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(studyNotes) { note ->
                    Card(
                        modifier = Modifier.fillMaxWidth().testTag("saved_note_card_${note.id}"),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    color = MaterialTheme.colorScheme.primaryContainer,
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = note.subject,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }

                                IconButton(
                                    onClick = { viewModel.deleteStudyNote(note.id) },
                                    modifier = Modifier.size(24.dp).testTag("delete_note_btn_${note.id}")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete note",
                                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f),
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }

                            Text(
                                text = note.question,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))

                            Text(
                                text = note.answer,
                                fontSize = 12.sp,
                                lineHeight = 16.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                            )

                            Text(
                                text = "Saved on: " + SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(Date(note.timestamp)),
                                fontSize = 9.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                                modifier = Modifier.align(Alignment.End)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProgressScreen(
    viewModel: StudyViewModel,
    quizHistory: List<QuizResultEntity>
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Deepak's Progress Reports 📈",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.primary
            )

            if (quizHistory.isNotEmpty()) {
                IconButton(
                    onClick = { viewModel.clearQuizHistory() },
                    modifier = Modifier.testTag("clear_history_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Clear Logs",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (quizHistory.isEmpty()) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.TrendingUp,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Report abhi khali hai!",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
                )
                Text(
                    text = "Aap jaise jaise quizzes solve karenge yahan aapka score cards automatic save honge progress track karne ke liye.",
                    textAlign = TextAlign.Center,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 4.dp),
                    lineHeight = 16.sp
                )
            }
        } else {
            // Stats overall
            val avgScore = quizHistory.map { it.score.toFloat() / it.totalQuestions.toFloat() }.average() * 100

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Average Performance",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = String.format(Locale.getDefault(), "%.1f%% Accuracy", avgScore),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .background(MaterialTheme.colorScheme.primary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "🎯",
                            fontSize = 22.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Quiz Attempt History Logs",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(quizHistory) { result ->
                    Card(
                        modifier = Modifier.fillMaxWidth().testTag("history_item_card_${result.id}"),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Surface(
                                        color = if (result.mode == "Entrance") MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.secondaryContainer,
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text(
                                            text = if (result.mode == "Entrance") "Entrance" else "Course",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }

                                    Text(
                                        text = SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault()).format(Date(result.timestamp)),
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = result.subject,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(
                                        if (result.score >= result.totalQuestions - 1) MathGreen.copy(alpha = 0.15f) else MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${result.score}/${result.totalQuestions}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = if (result.score >= result.totalQuestions - 1) MathGreen else MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
