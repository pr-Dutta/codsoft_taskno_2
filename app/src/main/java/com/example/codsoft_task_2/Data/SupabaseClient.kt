package com.example.codsoft_task_2.Data

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest

val supabase = createSupabaseClient(
    supabaseUrl = "https://bkubfjhduotouokeqyxm.supabase.co",
    supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImJrdWJmamhkdW90b3Vva2VxeXhtIiwicm9sZSI6ImFub24iLCJpYXQiOjE3MTc0MjQ3NTQsImV4cCI6MjAzMzAwMDc1NH0.eUB_OIk73RrZ0MGGMD6fcTYEe4Z0uHngY-K8guqKQts"
) {
    install(Postgrest)
}