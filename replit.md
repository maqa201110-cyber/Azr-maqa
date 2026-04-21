# Replit Agent Skills Library

## Overview

This repository is a Replit Agent Skills Library — a meta-repository containing skill definitions, templates, and configuration used by the Replit AI agent system.

## Project Structure

- `.local/skills/` — Core agent skills (workflows, database, deployment, canvas, etc.)
- `.local/secondary_skills/` — Specialized skill modules (seo, legal, meal-planner, etc.)
- `.local/state/` — Internal agent state (SQLite DB)
- `.agents/` — Agent configuration
- `server.js` — Minimal HTTP server for Replit preview (port 5000)

## Tech Stack

- **Runtime:** Node.js (built-in `http` module)
- **Environment:** Nix (stable-25_05 channel)
- **Port:** 5000

## Running

The application runs a simple static info page via `node server.js` on port 5000.

## Deployment

Configured for autoscale deployment using `node server.js`.
