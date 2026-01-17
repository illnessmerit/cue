## Goals

### Likelihood

> Are the likelihood scores guaranteed to match real-world frequencies?

No. The scores come from the model's distribution, not real-world counts. They're unlikely to match reality perfectly.

But they help favor natural phrasing over awkward or rare alternatives.

### Uniqueness

> Can the pipeline deduplicate sentences?

Yes. The pipeline uses cosine similarity and connected components to group related sentences and picks the one with the highest likelihood score from each group.

### Quantity

> How many sentences does the pipeline generate?

The goal is at least 1,000 sentences.

### Cost

> What is the budget for generating and ranking the sentences?

The goal is to stay under $1,000.

## Search

> Does `cue` parse sentences from a corpus?

No. There are issues with conversational datasets:

- Most datasets are too small. In a limited sample, common patterns are easily missed while outliers may be overrepresented.

- The settings are often artificial. SWITCHBOARD, for instance, features strangers who were told to discuss assigned topics over the phone.

Instead, `cue` mines the model's probability distribution.

> Does `cue` use an instruction-tuned model to find candidates?

No. Instruction-tuned models are fine-tuned to be helpful. That fine-tuning warps their probability distribution toward how an assistant should act rather than how humans talk.

Instead, `cue` uses a base model.

> Does `cue` fine-tune a base model?

No. Spontaneous conversation data is hard to find. Fine-tuning on artificially constructed data risks biasing the model toward the patterns of forced settings.

> What model does `cue` use to find and rank sentences?

`cue` uses `Qwen3-30B-A3B-Base`. At the time of selection, its instruction-tuned counterpart, `Qwen3-30B-A3B-Instruct-2507`, was the highest-ranking model with fewer than 100B parameters on LMArena. I use the rank of the instruction-tuned version as a proxy to judge the quality of the base model. Its low VRAM footprint lets me run it at full precision on a single GPU.

It also helps that the same family includes smaller models. I can iterate quickly on my development machine without worrying about inconsistencies when I move to production.

> Does `cue` use a base model with a prompt?

Yes. It uses the prompt `She's like, "`. There are a few reasons:

- Women outnumber men in the US, making `she` a more sensible default than `he`.

- The phrasing `She's like` primes the model for a more casual completion than `She says`.

- The opening quote primes the model for spoken dialogue.

> Does `cue` use multiple prompts to produce a dataset?

No. Combining samples from different contexts would likely lead to an unrepresentative distribution since the real world frequency of each context is unknown.

> Are the sentences guaranteed to be grammatical?

No. People often speak ungrammatically. The goal is to capture the sentences that people likely ask in conversation.

> Does an LLM as a judge assign the likelihood scores?

No. Using an LLM as a judge to rank the list isn't straightforward. A full pairwise comparison would be quadratic and expensive. A faster method like Elo has its own problems, like figuring out when the ratings have stabilized.

The likelihood scores are the log probabilities a model calculates during search.

> Does `cue` only find questions?

No. A question demands an answer, which creates a clear setup. But statements are cues, too. They're great for practicing how to react and build on a conversation.

If you just want to drill questions, you can filter for a question mark in a spreadsheet.

> Does `cue` filter out sentences that don't seem to stand on their own?

No. The goal is a realistic training set. Conversation is full of sentences that refer back to the previous turn.

## Deduplication

> What model does `cue` use to deduplicate sentences?

`cue` uses `Qwen3-Embedding-8B`. At the time of selection, this was the highest-ranking model on the MTEB STS benchmark that included a GitHub repository and a public evaluation methodology.

There is a fine-tuned version of this model called `Octen-Embedding-8B` that reported a slightly higher score. I passed on it because it lacked a repository and a public evaluation methodology.
