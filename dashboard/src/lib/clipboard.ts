export async function copyTextToClipboard(text: string): Promise<boolean> {
  const value = text.trim()
  if (!value) {
    return false
  }

  if (navigator.clipboard && window.isSecureContext) {
    try {
      await navigator.clipboard.writeText(value)
      return true
    } catch {
      // Fall through to the textarea fallback below. This covers browsers where
      // the Clipboard API exists but is blocked by permissions or context.
    }
  }

  return copyWithTextareaFallback(value)
}

function copyWithTextareaFallback(value: string): boolean {
  const textarea = document.createElement('textarea')
  textarea.value = value
  textarea.setAttribute('readonly', '')
  textarea.style.position = 'fixed'
  textarea.style.top = '0'
  textarea.style.left = '-9999px'
  textarea.style.opacity = '0'

  document.body.appendChild(textarea)
  textarea.focus()
  textarea.select()
  textarea.setSelectionRange(0, value.length)

  try {
    return document.execCommand('copy')
  } catch {
    return false
  } finally {
    document.body.removeChild(textarea)
  }
}
