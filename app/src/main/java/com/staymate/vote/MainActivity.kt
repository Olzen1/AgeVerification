package com.staymate.vote

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.staymate.vote.ui.theme.VOTETheme
import androidx.compose.foundation.layout.Row
import androidx.compose.ui.Alignment
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            VOTETheme {
                VoteScreen()
            }
        }
    }
}

@Composable
fun VoteScreen() { //to remember all the value
    val voteManager = remember { VoteManager() }
    var screen by remember { mutableIntStateOf(0) }
    var name by remember { mutableStateOf("") }
    var dob by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var selectedId by remember { mutableIntStateOf(-1) }
    var votedName by remember { mutableStateOf("") }
    var votedParty by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(70.dp)
    ) {
        when (screen) {
            0 -> {
                Text("Voter Age Verification")
                Spacer(Modifier.height(24.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Full Name") },
                    singleLine = true
                )
                Spacer(Modifier.height(16.dp))

                OutlinedTextField(
                    value = dob,
                    onValueChange = { dob = it },
                    label = { Text("Date of Birth (dd/MM/yyyy)") },
                    singleLine = true
                )
                Spacer(Modifier.height(24.dp))

                Button(onClick = { //display the text after user add their age
                    when (val result = voteManager.verifyAge(dob)) {
                        is VoteManager.AgeResult.Valid -> {
                            message = ""
                            screen = 1
                        }
                        is VoteManager.AgeResult.TooYoung -> {
                            message = "You are ${result.age} years old. Must be 21+."
                        }
                        is VoteManager.AgeResult.Error -> {
                            message = result.message
                        }
                    }
                }) {
                    Text("Verify Age")
                }

                if (message.isNotBlank()) {
                    Spacer(Modifier.height(16.dp))
                    Text(message)
                }
            }

            1 -> {
                Text("Welcome, $name")
                Spacer(Modifier.height(24.dp))
                Text("Select a candidate:")
                Spacer(Modifier.height(12.dp))

                voteManager.getCandidates().forEach { candidate ->

                    RadioButton(
                        selected = selectedId == candidate.id,
                        onClick = { selectedId = candidate.id }
                    )

                    Text("  ${candidate.name} (${candidate.party})")
                    Spacer(Modifier.height(8.dp))
                }

                Spacer(Modifier.height(24.dp))

                Button(onClick = {
                    if (selectedId == -1) {
                        message = "Please select a candidate."
                    } else {
                        when (val result = voteManager.castVote(selectedId)) {
                            is VoteManager.VoteResult.Success -> {
                                votedName = result.candidateName
                                votedParty = result.candidateParty
                                message = ""
                                screen = 2
                            }
                            is VoteManager.VoteResult.Error -> {
                                message = result.message
                            }
                        }
                    }
                }) {
                    Text("Vote")
                }

                if (message.isNotBlank()) {
                    Spacer(Modifier.height(16.dp))
                    Text(message)
                }
            }

            2 -> {
                Text("Vote Cast Successfully")
                Spacer(Modifier.height(24.dp))
                Text("You voted for: $votedName ($votedParty)")
                Spacer(Modifier.height(24.dp))

                Button(onClick = {
                    name = ""
                    dob = ""
                    message = ""
                    selectedId = -1
                    votedName = ""
                    votedParty = ""
                    screen = 0
                }) {
                    Text("New Voter")
                }
            }
        }
    }
}